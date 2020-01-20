package pl.allegro.tech.servicemesh.envoycontrol.reliability

import io.micrometer.core.instrument.simple.SimpleMeterRegistry
import org.junit.jupiter.api.Test
import pl.allegro.tech.servicemesh.envoycontrol.ControlPlane
import pl.allegro.tech.servicemesh.envoycontrol.EnvoyControlProperties
import pl.allegro.tech.servicemesh.envoycontrol.services.LocalityAwareServicesState
import reactor.core.publisher.Flux
import reactor.core.publisher.FluxSink

typealias ChangesSink = FluxSink<List<LocalityAwareServicesState>>

class WarmingClustersTest {


    @Test
    fun `should not end up with warming clusters after update`() {

        val properties = EnvoyControlProperties()
        val meterRegistry = SimpleMeterRegistry()


        var serviceChangesSink: ChangesSink
        val serviceChanges = Flux.create { sink: ChangesSink -> serviceChangesSink = sink }

        val controlPlane = ControlPlane.builder(properties, meterRegistry)
            .build(serviceChanges)


    }
}