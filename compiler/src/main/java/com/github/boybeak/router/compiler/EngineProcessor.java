package com.github.boybeak.router.compiler;

import com.github.boybeak.router.annotation.*;
import com.squareup.javapoet.*;

import javax.annotation.processing.AbstractProcessor;
import javax.annotation.processing.RoundEnvironment;
import javax.annotation.processing.SupportedAnnotationTypes;
import javax.lang.model.element.*;
import javax.lang.model.util.ElementFilter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;

@SupportedAnnotationTypes({
        "com.github.boybeak.router.annotation.Intent"
})
public class EngineProcessor extends AbstractProcessor {

    private static final String PACKAGE = "com.github.boybeak.router",
            CLASS_SIMPLE_NAME = "AppRouter", S_ROUTER = "sRouter", CONTEXT = "context";

    @Override
    public boolean process(Set<? extends TypeElement> set, RoundEnvironment roundEnvironment) {

        generateAppRouter(roundEnvironment);

        return true;
    }

    private void generateAppRouter(RoundEnvironment roundEnvironment) {

        Set<? extends Element> elementSet = roundEnvironment.getElementsAnnotatedWith(Intent.class);
        List<? extends ExecutableElement> itElements = new ArrayList<>(ElementFilter.methodsIn(elementSet));

        TypeSpec.Builder appRouterTypeBuilder = TypeSpec.classBuilder(CLASS_SIMPLE_NAME)
                .addModifiers(Modifier.PUBLIC)
                .superclass(ClassName.get(PACKAGE, "AbsRouterEngine"))
                .addField(singleInstanceField())
                .addMethod(singleInstance())
                .addMethod(constructor());

        for (ExecutableElement ee : itElements) {
            Intent it = ee.getAnnotation(Intent.class);
            appRouterTypeBuilder.addMethod(methodSpec(ee, it));
        }

        JavaFile appRouter = JavaFile.builder(PACKAGE, appRouterTypeBuilder.build()).build();
        try {
            appRouter.writeTo(processingEnv.getFiler());
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private ClassName getAppRouterClassName() {
        return ClassName.get(PACKAGE, CLASS_SIMPLE_NAME);
    }

    private MethodSpec singleInstance() {
        return MethodSpec.methodBuilder("getInstance")
                .addModifiers(Modifier.PUBLIC, Modifier.STATIC)
                .returns(getAppRouterClassName())
                .addCode("if($L == null) {\n", S_ROUTER)
                .addCode("\tsynchronized($L.class) {\n", CLASS_SIMPLE_NAME)
                .addCode("\t\tif($L == null) {\n", S_ROUTER)
                .addCode("\t\t\t$L = new $T();\n", S_ROUTER, getAppRouterClassName())
                .addCode("\t\t}\n").addCode("\t}\n").addCode("}\n")
                .addCode("return $L;\n", S_ROUTER)
                .build();
    }

    private FieldSpec singleInstanceField() {
        return FieldSpec.builder(getAppRouterClassName(), S_ROUTER,
                Modifier.PRIVATE, Modifier.STATIC).build();
    }

    private MethodSpec constructor() {
        return MethodSpec.constructorBuilder()
                .addModifiers(Modifier.PRIVATE)
                .build();
    }

    private MethodSpec methodSpec(ExecutableElement ee, Intent it) {
        List<? extends VariableElement> params = ee.getParameters();
        MethodSpec.Builder mb = MethodSpec.methodBuilder(ee.getSimpleName().toString())
                .addModifiers(Modifier.PUBLIC)
                .returns(ClassName.get(ee.getReturnType()));
        if (it.forResult()) {
            mb.addParameter(
                    ParameterSpec.builder(
                            ClassName.get("android.app", "Activity"), CONTEXT).build());
        } else {
            mb.addParameter(
                    ParameterSpec.builder(
                            ClassName.get("android.content", "Context"), CONTEXT).build());
        }

        TypeName itCN = Helper.intentClassName();
        String intent = ee.getSimpleName() + "It";
        mb.addCode("$T $L = new $T();\n", itCN, intent, itCN);

        if (it.flags().length > 0) {
            int[] flags = it.flags();
            for (int flag : flags) {
                mb.addCode("$L.addFlags($L);\n", intent, flag);
            }
        }
        mb.addCode("\n");
        if (it.categories().length > 0) {
            String[] categories = it.categories();
            for (String category : categories) {
                mb.addCode("$L.addCategory(\"$L\");\n", intent, category);
            }
        }
        mb.addCode("\n");
        if (Helper.isActivityIt(it)) {
            mb.addCode("$L.setClass($L, $L.class);\n\n", intent, CONTEXT, Helper.getActivity(it));
        } else if (Helper.isActionIt(it)) {
            mb.addCode("$L.setAction(\"$L\");\n\n", intent, it.action());
        }
        String pkg = it.packageName();
        if (!"".equals(pkg)) {
            mb.addCode("$L.setPackage(\"$L\");\n\n", intent, pkg);
        }
        String itType = it.type();
        if (!"".equals(itType)) {
            if (it.normalized()) {
                mb.addCode("$L.setTypeAndNormalize(\"$L\");\n\n", intent, itType);
            } else {
                mb.addCode("$L.setType(\"$L\");\n\n", intent, itType);
            }
        }

        for (VariableElement ve : params) {
            TypeName type = TypeName.get(ve.asType());
            String varName = ve.getSimpleName().toString();

            mb.addParameter(
                    ParameterSpec.builder(type, varName).build()
            );
            Data data = ve.getAnnotation(Data.class);
            if (data != null) {
                if (!Helper.isUriType(type)) {
                    throw new RuntimeException("The parameter " + varName + " is assigned to setData or setDataAndType, but it's not Uri");
                }
                if (data.normalized()) {
                    String t = data.type();
                    if ("".equals(t)) {
                        mb.addCode("$L.setDataAndNormalize($L);\n\n", intent, varName);
                    } else {
                        mb.addCode("$L.setDataAndTypeAndNormalize($L, \"$L\");\n\n", intent, varName, t);
                    }
                } else {
                    String t = data.type();
                    if ("".equals(t)) {
                        mb.addCode("$L.setData($L);\n\n", intent, varName);
                    } else {
                        mb.addCode("$L.setDataAndType($L, \"$L\");\n\n", intent, varName, t);
                    }
                }
                continue;
            }

            Extras extras = ve.getAnnotation(Extras.class);
            if (extras != null) {
                if (Helper.isExtrasType(type)) {
                    mb.addCode("$L.putExtras($L);\n\n", intent, varName);
                } else {
                    throw new RuntimeException("The parameter " + varName + " is assigned to putExtras, but it's not Bundle nor Intent");
                }
                continue;
            }

            String keyName;
            Key key = ve.getAnnotation(Key.class);
            if (key == null || "".equals(key.value())) {
                keyName = varName;
            } else {
                keyName = key.value();
            }

            if (Helper.isIntegerCollection(key)) {
                String varListName = varName + "IntegerList";
                mb.addCode("$T<$T> $L = new $T<>($L);\n", ArrayList.class, Integer.class, varListName, ArrayList.class, varName);
                mb.addCode("$L.putIntegerArrayListExtra(\"$L\", $L);\n\n", intent, keyName, varListName);
            } else if (Helper.isStringCollection(key)) {
                String varListName = varName + "StringList";
                mb.addCode("$T<$T> $L = new $T<>($L);\n", ArrayList.class, String.class, varListName, ArrayList.class, varName);
                mb.addCode("$L.putStringArrayListExtra(\"$L\", $L);\n\n", intent, keyName, varListName);
            } else if (Helper.isCharSequenceCollection(key)) {
                String varListName = varName + "CharSequenceList";
                mb.addCode("$T<$T> $L = new $T<>();\n", ArrayList.class, CharSequence.class, varListName, ArrayList.class);
                mb.addCode("$L.addAll($L);\n", varListName, varName);
                mb.addCode("$L.putCharSequenceArrayListExtra(\"$L\", $L);\n\n", intent, keyName, varListName);
            } else if (Helper.isParcelableCollection(key)) {
                String varListName = varName + "ParcelableList";
                mb.addCode("$T<? extends $T> $L = new $T<>($L);\n", ArrayList.class, Helper.parcelableTypeName(), varListName, ArrayList.class, varName);
                mb.addCode("$L.putParcelableArrayListExtra(\"$L\", $L);\n\n", intent, keyName, varListName);
            } if (Helper.isCommonTypes(key)) {
                mb.addCode("$L.putExtra(\"$L\", $L);\n\n", intent, keyName, varName);
            }
        }

        addInterceptors(mb, it, intent);
        if (it.forResult()) {
            mb.addCode("$L.startActivityForResult($L, $L);", CONTEXT, intent, it.requestCode());
        } else {
            mb.addCode("$L.startActivity($L);", CONTEXT, intent);
        }

        return mb.build();
    }

    private void addInterceptors(MethodSpec.Builder mb, Intent it, String intentName) {
        TypeName[] typeNames = Helper.getInterceptors(it);
        String interceptorClzArray = "interceptorClzArray";
        String interceptorName = "Interceptor";

        mb.addCode("Class<? extends $T>[] $L = new Class[$L];\n",
                ClassName.get(PACKAGE, interceptorName),
                interceptorClzArray, typeNames.length);

        /*mb.addCode("for (int i = 0; i < $L.length; i++) {\n", interceptorClzArray)
                .addCode("}\n");*/
        for (int i = 0; i < typeNames.length; i++) {
            TypeName tn = typeNames[i];
            String interceptorVarName = "interceptor" + i;
            mb.addCode("$L $L = findInterceptor($L[$L]);\n", interceptorName, interceptorVarName, interceptorClzArray, i)
                    .addCode("if ($L == null) {\n", interceptorVarName)
                    .addCode("\t$L = new $T();\n", interceptorVarName, tn)
                    .addCode("\tputInterceptor($L);\n", interceptorVarName)
                    .addCode("}\n")
                    .addCode("if ($L.onIntercept($L, $L)) {\n", interceptorVarName, CONTEXT, intentName)
                    .addCode("\treturn;\n")
                    .addCode("}\n");
        }

    }

}
