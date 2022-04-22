package org.dbpedia.databus.mods.worker.springboot;

import org.dbpedia.databus.mods.worker.springboot.controller.WorkerApiProfile;

import java.lang.annotation.*;

@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface EnableModWorkerApi {

    String version() default  "";

    String apiEndpoint() default "";

    WorkerApiProfile profile() default WorkerApiProfile.Basic;
}
