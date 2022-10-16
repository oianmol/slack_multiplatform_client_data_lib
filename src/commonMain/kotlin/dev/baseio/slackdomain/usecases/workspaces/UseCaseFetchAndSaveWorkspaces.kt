package dev.baseio.slackdomain.usecases.workspaces

import dev.baseio.slackdata.protos.KMSKWorkspace
import dev.baseio.slackdomain.datasources.local.workspaces.SKLocalDataSourceReadWorkspaces
import dev.baseio.slackdomain.datasources.local.workspaces.SKLocalDataSourceWriteWorkspaces
import dev.baseio.slackdomain.datasources.remote.workspaces.SKNetworkDataSourceReadWorkspaces
import dev.baseio.slackdomain.model.workspaces.DomainLayerWorkspaces
import kotlinx.coroutines.flow.*

class UseCaseFetchAndSaveWorkspaces(
    private val skNetworkDataSourceReadWorkspaces: SKNetworkDataSourceReadWorkspaces,
    private val skLocalDataSourceWriteWorkspaces: SKLocalDataSourceWriteWorkspaces,
) {
    suspend operator fun invoke() {
        kotlin.runCatching {
            val kmSKWorkspaces = skNetworkDataSourceReadWorkspaces.getWorkspaces()
            skLocalDataSourceWriteWorkspaces.saveWorkspaces(kmSKWorkspaces.workspacesList.map { kmskWorkspace ->
                kmskWorkspace.toSKWorkspace()
            })
        }
    }
}

fun KMSKWorkspace.toSKWorkspace(): DomainLayerWorkspaces.SKWorkspace {
    return DomainLayerWorkspaces.SKWorkspace(this.uuid, this.name, this.domain, this.picUrl, this.modifiedTime)
}
