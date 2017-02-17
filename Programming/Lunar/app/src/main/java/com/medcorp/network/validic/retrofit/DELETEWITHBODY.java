package com.medcorp.network.validic.retrofit;

/**
 * Created by gaillysu on 16/3/18.
 */
import java.lang.annotation.Documented;
import java.lang.annotation.Retention;
import java.lang.annotation.Target;

import retrofit.http.RestMethod;

import static java.lang.annotation.ElementType.METHOD;
import static java.lang.annotation.RetentionPolicy.RUNTIME;

@Documented
@Target(METHOD)
@Retention(RUNTIME)
@RestMethod(value = "DELETE", hasBody = true)
public @interface DELETEWITHBODY {
    String value();
}
