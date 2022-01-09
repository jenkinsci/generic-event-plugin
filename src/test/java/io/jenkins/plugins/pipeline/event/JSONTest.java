package io.jenkins.plugins.pipeline.event;

import org.junit.Test;
import org.kohsuke.stapler.export.Exported;
import org.kohsuke.stapler.export.ExportedBean;

import net.sf.json.JSONObject;

public class JSONTest {
    @Test
    public void jsonTest() {
        Event data = new Event.EventBuilder().data(new ExportedData(10, "johnniang")).build();
        String dataJSON = JSONObject.fromObject(data, new EventJsonConfig()).toString(4);
        System.out.println(dataJSON);

        data = new Event.EventBuilder().data(new Data()).build();
        dataJSON = JSONObject.fromObject(data, new EventJsonConfig()).toString(4);
        System.out.println(dataJSON);
    }

    @ExportedBean
    public class ExportedData {

        @Exported
        private int age;

        private String name;

        public ExportedData(int age, String name) {
            this.age = age;
            this.name = name;
        }

        public ExportedData(int age) {
            this.age = age;
        }

        @Exported
        public int getAge() {
            return age;
        }

        public void setAge(int age) {
            this.age = age;
        }

        public String getName() {
            return name;
        }

        public void setName(String name) {
            this.name = name;
        }
    }

    public class Data {

        private String name;

        private Object data;

        public String getName() {
            return name;
        }

        public void setName(String name) {
            this.name = name;
        }

        public Object getData() {
            return data;
        }

        public void setData(Object data) {
            this.data = data;
        }
    }

}
