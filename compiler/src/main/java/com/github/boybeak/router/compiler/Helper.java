package com.github.boybeak.router.compiler;

import com.github.boybeak.router.annotation.Intent;
import com.github.boybeak.router.annotation.Key;
import com.github.boybeak.router.annotation.ExtraType;
import com.squareup.javapoet.ClassName;
import com.squareup.javapoet.TypeName;

import javax.lang.model.type.MirroredTypeException;
import javax.lang.model.type.MirroredTypesException;
import javax.lang.model.type.TypeMirror;
import java.util.List;

public class Helper {

    private static final TypeName PARCELABLE = ClassName.get("android.os", "Parcelable"),
            /*CHARSEQUENCE = TypeName.get(CharSequence.class),*/ STRING = TypeName.get(String.class),
            /*SERIALIZABLE = TypeName.get(Serializable.class),*/ BOOLEAN_ARRAY = TypeName.get(boolean[].class),
            BYTE_ARRAY = TypeName.get(byte[].class), CHAR_ARRAY = TypeName.get(char[].class),
            /*CHARSEQUENCE_ARRAY = TypeName.get(CharSequence[].class),*/ DOUBLE_ARRAY = TypeName.get(double[].class),
            FLOAT_ARRAY = TypeName.get(float[].class), INT_ARRAY = TypeName.get(int[].class),
            LONG_ARRAY = TypeName.get(long[].class), SHORT_ARRAY = TypeName.get(short[].class), STRING_ARRAY = TypeName.get(String[].class),
            /*PARCELABLE_ARRAY = ArrayTypeName.of(PARCELABLE),*/ BUNDLE = ClassName.get("android.os", "Bundle"),
            INTENT = ClassName.get("android.content", "Intent"), URI = ClassName.get("android.net", "Uri");

    public static TypeName intentClassName() {
        return INTENT;
    }
    public static TypeName parcelableTypeName() {
        return PARCELABLE;
    }

    public static boolean isActivityIt(Intent it) {
        return !getActivity(it).toString().equals("void");
    }

    public static boolean isActionIt(Intent it) {
        return !"".equals(it.action());
    }

    public static ClassName getActivity(Intent it) {
        TypeMirror clazzType;
        try {
            return ClassName.get(it.activity());
        } catch (MirroredTypeException mte) {
            clazzType = mte.getTypeMirror();
        }
        return Helper.getClassName(clazzType.toString());
    }

    private static String getPackage(String name) {
        int index = name.lastIndexOf('.');
        if (index < 0) {
            return "";
        }
        return name.substring(0, index);
    }
    private static String getSimpleName(String name) {
        return name.substring(name.lastIndexOf('.') + 1);
    }

    public static ClassName getClassName(String name) {
        return ClassName.get(getPackage(name), getSimpleName(name));
    }

    public static boolean isExtrasType(TypeName type) {
        return INTENT.equals(type) || BUNDLE.equals(type);
    }

    public static boolean isUriType(TypeName type) {
        return URI.equals(type);
    }

    public static TypeName[] getInterceptors(Intent it) {
        List<? extends TypeMirror> mirrors;
        TypeName[] typeNames;
        try {
            Class<?>[] interceptors = it.interceptors();
            typeNames = new TypeName[interceptors.length];
            for (int i = 0; i < typeNames.length; i++) {
                typeNames[i] = TypeName.get(interceptors[i]);
            }
        } catch (MirroredTypesException mte) {
            mirrors = mte.getTypeMirrors();
            typeNames = new TypeName[mirrors.size()];
            for (int i = 0; i < mirrors.size(); i++) {
                typeNames[i] = TypeName.get(mirrors.get(i));
            }
        }
        return typeNames;
    }

    public static boolean isCommonTypes(Key key) {
        return key == null || key.extraType() == ExtraType.BASIC_OR_ARRAY;
    }

    public static boolean isCharSequenceCollection(Key key) {
        return key != null && key.extraType() == ExtraType.CHAR_SEQUENCE_LIST;
    }

    public static boolean isIntegerCollection(Key key) {
        return key != null && key.extraType() == ExtraType.INTEGER_LIST;
    }

    public static boolean isStringCollection(Key key) {
        return key != null && key.extraType() == ExtraType.STRING_LIST;
    }

    public static boolean isParcelableCollection(Key key) {
        return key != null && key.extraType() == ExtraType.PARCELABLE_LIST;
    }

}
