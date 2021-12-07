package org.dbpedia.databus.mods.worker.springboot;

import java.lang.annotation.*;

@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface ModWorkerProcessor {

}
