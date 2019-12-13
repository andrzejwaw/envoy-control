package pl.allegro.tech.servicemesh.envoycontrol.server.callbacks

import com.google.common.util.concurrent.ThreadFactoryBuilder
import io.envoyproxy.controlplane.server.DiscoveryServerCallbacks
import io.envoyproxy.envoy.api.v2.DiscoveryRequest
import org.slf4j.LoggerFactory
import pl.allegro.tech.servicemesh.envoycontrol.groups.NodeMetadata
import pl.allegro.tech.servicemesh.envoycontrol.services.ServiceName
import pl.allegro.tech.servicemesh.envoycontrol.snapshot.SnapshotProperties
import java.util.concurrent.ConcurrentHashMap
import java.util.concurrent.Executors
import java.util.concurrent.TimeUnit

class ConnectedEnvoyStatusCallback(val properties: SnapshotProperties) : DiscoveryServerCallbacks {

    private val logger = LoggerFactory.getLogger(ConnectedEnvoyStatusCallback::class.java)
    private val connectedEnvoys: ConcurrentHashMap<Long, ServiceName> = ConcurrentHashMap()

    private val task: Runnable = Runnable {
        logger.info("Current services connected: {}", connectedEnvoys.values.filter { it.isNotBlank() }.distinct())
    }
    private val executor = Executors.newSingleThreadScheduledExecutor(
        ThreadFactoryBuilder().setNameFormat("connected-services-thread-pool").build()
    )

    companion object {
        private const val EMPTY_SERVICE = ""
        private const val INITIAL_DELAY = 300L
        private const val LOG_PERIOD = 10L
    }

    init {
        executor.scheduleAtFixedRate(task, INITIAL_DELAY, LOG_PERIOD, TimeUnit.SECONDS)
    }

    override fun onStreamRequest(streamId: Long, request: DiscoveryRequest?) {
        val serviceName = request?.node?.let { it ->
            val metadata = NodeMetadata(it.metadata, properties)
            metadata.serviceName ?: EMPTY_SERVICE
        } ?: EMPTY_SERVICE
        connectedEnvoys[streamId] = serviceName
    }

    override fun onStreamOpen(streamId: Long, typeUrl: String?) {
        connectedEnvoys[streamId] = EMPTY_SERVICE
    }

    override fun onStreamClose(streamId: Long, typeUrl: String?) {
        connectedEnvoys.remove(streamId)
    }

    override fun onStreamCloseWithError(streamId: Long, typeUrl: String?, error: Throwable?) {
        connectedEnvoys.remove(streamId)
    }
}