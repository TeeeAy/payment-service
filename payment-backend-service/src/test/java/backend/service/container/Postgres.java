package backend.service.container;


import backend.service.test.util.PropertiesReaderUtil;
import com.github.dockerjava.api.model.ExposedPort;
import com.github.dockerjava.api.model.PortBinding;
import com.github.dockerjava.api.model.Ports;
import org.testcontainers.containers.PostgreSQLContainer;

public class Postgres {


    private Postgres() {
    }


    public static final PostgreSQLContainer<?> CONTAINER = new PostgreSQLContainer<>("postgres:13.3")
            .withUsername(PropertiesReaderUtil.getProperty("spring.datasource.username"))
            .withPassword("spring.datasource.password")
            .withCreateContainerCmdModifier(cmd ->
                    cmd.getHostConfig()
                            .withPortBindings(new PortBinding(Ports.Binding.bindPort(5432), ExposedPort.tcp(5433))));


}
