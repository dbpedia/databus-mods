package org.dbpedia.databus.mods.worker.springboot;

import org.dbpedia.databus.mods.worker.springboot.controller.WorkerApiProfile;
import org.springframework.context.annotation.ImportSelector;
import org.springframework.core.annotation.AnnotationAttributes;
import org.springframework.core.type.AnnotationMetadata;

public class ModWorkerProcessor implements ImportSelector {

    @Override
    public String[] selectImports(AnnotationMetadata annotationMetadata) {
        AnnotationAttributes attributes =
                AnnotationAttributes.fromMap(
                        annotationMetadata.getAnnotationAttributes
                                (EnableModWorkerApi.class.getName(), false));
        String criteria = attributes.getString("profile");
        if (criteria.equals(WorkerApiProfile.Basic)) {
            return new String[]{"enableannot.selector.SomeBeanConfigurationDefault"};
        } else {
            return new String[]{"enableannot.selector.SomeBeanConfigurationType1"};
        }
    }
}