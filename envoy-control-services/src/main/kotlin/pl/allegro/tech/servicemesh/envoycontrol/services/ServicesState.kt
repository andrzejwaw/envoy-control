package pl.allegro.tech.servicemesh.envoycontrol.services

typealias ServiceName = String

data class ServicesState(val serviceNameToInstances: Map<ServiceName, ServiceInstances> = emptyMap()) {
    var currentChange: Set<Change> = emptySet()

    operator fun get(serviceName: ServiceName): ServiceInstances? = serviceNameToInstances[serviceName]

    fun hasService(serviceName: String): Boolean = serviceNameToInstances.containsKey(serviceName)
    fun serviceNames(): Set<ServiceName> = serviceNameToInstances.keys
    fun allInstances(): Collection<ServiceInstances> = serviceNameToInstances.values

    fun remove(serviceName: ServiceName): ServicesState {
        // TODO: https://github.com/allegro/envoy-control/issues/11
        return change(ServiceInstances(serviceName, instances = emptySet()), Action.REMOVE)
    }

    fun add(serviceName: ServiceName): ServicesState =
        if (serviceNameToInstances.containsKey(serviceName)) this
        else change(ServiceInstances(serviceName, instances = emptySet()), Action.ADD)

    fun change(serviceInstances: ServiceInstances, action: Action = Action.UPDATE): ServicesState {
        return if (serviceNameToInstances[serviceInstances.serviceName] == serviceInstances)
            this
        else {
            val copy = copy(
                serviceNameToInstances = serviceNameToInstances + (serviceInstances.serviceName to serviceInstances)
            )
            copy.currentChange = setOf(Change(action = action, serviceName = serviceInstances.serviceName))
            copy
        }
    }

    enum class Action {
        ADD, UPDATE, REMOVE
    }

    class Change(val action: Action, val serviceName: ServiceName)
}
