package frc.robot.config;

import java.io.IOException;
import java.util.Map;

import com.google.common.base.Charsets;
import com.google.common.io.Resources;
import com.hubspot.jinjava.Jinjava;
import com.hubspot.jinjava.interpret.JinjavaInterpreter;
import com.hubspot.jinjava.lib.filter.Filter;

import org.yaml.snakeyaml.Yaml;

public class ConfigReader {
    /**
     * Read config from resources
     */
    public static Config readConfig(String configPath) throws IOException {
        Jinjava jinjava = new Jinjava();

        // add a new "int" filter to convert any number to an int
        jinjava.getGlobalContext().registerFilter(new Filter() {

            @Override
            public String getName() {
                return "int";
            }

            @Override
            public Object filter(Object var, JinjavaInterpreter interpreter, String... args) {
                return Double.valueOf(var.toString()).intValue();
            }
            
        });
        
        Yaml yaml = new Yaml();

        // First load the yaml file as a string template
        String template = Resources.toString(Resources.getResource(configPath), Charsets.UTF_8);

        // parse the yaml file as a Map, so we can call "render" on it and render any
        // jinja2 template stuff
        Map<String, Object> variables = yaml.load(template);
        String renderedTemplate = jinjava.render(template, variables);

        // Now load the config in as a Config object, from the rendered template with
        // expanded variables and calculations
        Config config = yaml.loadAs(renderedTemplate, Config.class);

        return config;
    }

}