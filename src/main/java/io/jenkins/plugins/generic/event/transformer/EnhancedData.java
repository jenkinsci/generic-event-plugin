package io.jenkins.plugins.generic.event.transformer;

import org.kohsuke.stapler.export.Exported;
import org.kohsuke.stapler.export.ExportedBean;

/**
 * EnhancedData has ability to merge raw data into current object.
 *
 * @param <RAW> Type of raw data, must be annotated by @ExportedBean.
 * @author johnniang
 */
@ExportedBean
public abstract class EnhancedData<RAW> {

    @Exported(name = "raw", merge = true)
    public abstract RAW getRaw();

}
