import com.fasterxml.jackson.core.JsonProcessingException;
import com.lucaf.zkm.ZkmConfig;
import com.lucaf.zkm.ZkmGenerator;

public class GeneratorTest {
    public static void main(String[] args) {
        ZkmConfig config = new ZkmConfig();
        ZkmGenerator generator = new ZkmGenerator(config);
        generator.addClassPath("/test/to/lib.jar");
        generator.addClassPath("\\test\\to\\lib2.jar");
        try {
            System.out.println(generator.generateConfig());
        } catch (JsonProcessingException e) {
            throw new RuntimeException(e);
        }
    }
}
