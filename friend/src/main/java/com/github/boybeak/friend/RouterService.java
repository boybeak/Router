package com.github.boybeak.friend;

import android.net.Uri;
import android.text.SpannableString;
import com.github.boybeak.router.annotation.*;

import java.util.List;

public interface RouterService {
    @Intent(action = android.content.Intent.ACTION_CALL,
            interceptors = {SessionInterceptor.class,
                    RoleInterceptor.class},
            type = "image/*",
            normalized = true,
            flags = {android.content.Intent.FLAG_ACTIVITY_NEW_TASK},
            categories = {android.content.Intent.CATEGORY_DEFAULT})
    void gotoTarget(@Key(extraType = ExtraType.INTEGER_LIST) List<Integer> ids,
                    @Key(extraType = ExtraType.PARCELABLE_LIST) List<User> users,
                    @Key(extraType = ExtraType.CHAR_SEQUENCE_LIST) List<SpannableString> ss,
                    @Key(extraType = ExtraType.STRING_LIST) List<String> names,
                    User user, @Extras android.content.Intent extras,
                    @Data(type = "image/*", normalized = true) Uri uri);
}