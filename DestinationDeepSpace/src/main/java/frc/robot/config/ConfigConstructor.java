package frc.robot.config;

import javax.script.ScriptEngine;
import javax.script.ScriptEngineManager;
import javax.script.ScriptException;

import org.yaml.snakeyaml.constructor.AbstractConstruct;
import org.yaml.snakeyaml.constructor.Constructor;
import org.yaml.snakeyaml.nodes.Node;
import org.yaml.snakeyaml.nodes.ScalarNode;
import org.yaml.snakeyaml.nodes.Tag;

/**
 * TODO: Remove this, I don't think we need it
 */
public class ConfigConstructor extends Constructor {
    public ConfigConstructor() {
        this.yamlConstructors.put(new Tag("!evalDouble"), new ConstructEvalDouble());
    }

    private class ConstructEvalDouble extends AbstractConstruct {
        public Object construct(Node node) {
            String val = ((ScalarNode)node).getValue();
            return evalDoubleExpression(val);
        }
    }

    public static double evalDoubleExpression(String expr) {
        ScriptEngineManager manager = new ScriptEngineManager();
        ScriptEngine engine = manager.getEngineByName("js");
        try {
            return (double) engine.eval(expr);
        } catch (ScriptException e) {
            System.err.println("Failed to evaluate expression to double: " + expr);
            // e.printStackTrace();
            // Better to throw here and kill the program at load time rather than wonder why
            // some config value is showing 0.
            throw new RuntimeException(e);
        }
    }
}