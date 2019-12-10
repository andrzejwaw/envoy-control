package pl.allegro.tech.servicemesh.envoycontrol.services

typealias ServiceName = String

data class ServicesState(val serviceNameToInstances: Map<ServiceName, ServiceInstances> = emptyMap()) {
    var currentChange: Set<String> = emptySet()

    operator fun get(serviceName: ServiceName): ServiceInstances? = serviceNameToInstances[serviceName]

    fun hasService(serviceName: String): Boolean = serviceNameToInstances.containsKey(serviceName)
    fun serviceNames(): Set<ServiceName> = serviceNameToInstances.keys
    fun allInstances(): Collection<ServiceInstances> = serviceNameToInstances.values

    fun remove(serviceName: ServiceName): ServicesState {
        // TODO: https://github.com/allegro/envoy-control/issues/11
        return change(ServiceInstances(serviceName, instances = emptySet()))
    }

    fun add(serviceName: ServiceName): ServicesState =
        if (serviceNameToInstances.containsKey(serviceName)) this
        else change(ServiceInstances(serviceName, instances = emptySet()))

    fun change(serviceInstances: ServiceInstances): ServicesState {
        return if (serviceNameToInstances[serviceInstances.serviceName] == serviceInstances)
            this
        else {
            val copy = copy(
                serviceNameToInstances = serviceNameToInstances + (serviceInstances.serviceName to serviceInstances)
                )
            copy.currentChange = setOf(serviceInstances.serviceName)
            copy
        }
    }
}
