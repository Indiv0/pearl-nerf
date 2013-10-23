package in.nikitapek.pearlnerf.util;

import com.amshulman.typesafety.util.ParameterizedTypeImpl;
import com.google.gson.reflect.TypeToken;
import org.javatuples.Pair;

import java.lang.reflect.Type;

@SuppressWarnings("rawtypes")
public final class SupplementaryTypes {
    public static final Type AUTO_CLICK_TRACKING_INFO;

    static {
        ParameterizedTypeImpl t = new ParameterizedTypeImpl(new TypeToken<Pair>() {
        }.getType());
        t.addParamType(new TypeToken<Integer>() {
        }.getType());
        t.addParamType(new TypeToken<Long>() {
        }.getType());

        AUTO_CLICK_TRACKING_INFO = t;
    }
}
