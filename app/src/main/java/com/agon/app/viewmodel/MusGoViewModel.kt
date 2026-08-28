package com.agon.app.viewmodel

import androidx.lifecycle.ViewModel
import com.agon.app.data.Agent
import com.agon.app.data.AgentTask
import com.agon.app.data.AIProvider
import com.agon.app.data.AttachmentSource
import com.agon.app.data.AuditEvent
import com.agon.app.data.Blueprint
import com.agon.app.data.BuildArtifact
import com.agon.app.data.ChatAttachment
import com.agon.app.data.ChatMessage
import com.agon.app.data.ChatSession
import com.agon.app.data.Deployment
import com.agon.app.data.GitHubPat
import com.agon.app.data.GitHubRepo
import com.agon.app.data.Handover
import com.agon.app.data.MemoryEntry
import com.agon.app.data.MessageRole
import com.agon.app.data.MessageStatus
import com.agon.app.data.Project
import com.agon.app.data.ProviderApiKey
import com.agon.app.data.ProviderStatus
import com.agon.app.data.PullRequest
import com.agon.app.data.RoutingMode
import com.agon.app.data.SampleData
import com.agon.app.data.Sandbox
import com.agon.app.data.SandboxLog
import com.agon.app.data.SandboxRuntime
import com.agon.app.data.SecurityPosture
import com.agon.app.data.Skill
import com.agon.app.data.TerminalKind
import com.agon.app.data.TerminalSession
import com.agon.app.data.TerminalState
import com.agon.app.data.UserProfile
import com.agon.app.data.Workflow
import com.agon.app.data.WorkflowState
import com.agon.app.data.WorkflowStep
import com.agon.app.data.StepKind
import com.agon.app.data.StepState
import com.agon.app.data.ProviderType
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import java.util.UUID

data class MusGoUiState(
    val user: UserProfile = SampleData.user,
    val providers: List<AIProvider> = SampleData.providers,
    val projects: List<Project> = SampleData.projects,
    val agents: List<Agent> = SampleData.agents,
    val tasks: List<AgentTask> = SampleData.tasks,
    val blueprints: List<Blueprint> = SampleData.blueprints,
    val skills: List<Skill> = SampleData.skills,
    val pats: List<GitHubPat> = SampleData.pats,
    val repos: List<GitHubRepo> = SampleData.repos,
    val pullRequests: List<PullRequest> = SampleData.pullRequests,
    val sandboxes: List<Sandbox> = SampleData.sandboxes,
    val sandboxLogs: List<SandboxLog> = SampleData.sandboxLogs,
    val terminals: List<TerminalSession> = SampleData.terminals,
    val workflows: List<Workflow> = SampleData.workflows,
    val memoryEntries: List<MemoryEntry> = SampleData.memoryEntries,
    val handovers: List<Handover> = SampleData.handovers,
    val artifacts: List<BuildArtifact> = SampleData.artifacts,
    val deployments: List<Deployment> = SampleData.deployments,
    val auditEvents: List<AuditEvent> = SampleData.auditEvents,
    val security: SecurityPosture = SampleData.security,

    val chatSessions: List<ChatSession> = SampleData.chatSessions,
    val chatMessagesBySession: Map<String, List<ChatMessage>> = mapOf(
        "c-1" to SampleData.buildSampleMessages("c-1"),
        "c-2" to SampleData.buildSampleMessages("c-2"),
        "c-3" to SampleData.buildSampleMessages("c-3"),
        "c-4" to SampleData.buildSampleMessages("c-4"),
    ),
    val activeChatSessionId: String = "c-1",
    val activeRoutingMode: RoutingMode = RoutingMode.AUTO_BALANCED,

    val selectedProviderId: String = "p-2",
    val selectedModelId: String = "claude-3.5-sonnet",

    val toasts: List<ToastEvent> = emptyList(),
)

data class ToastEvent(val id: String, val message: String, val tone: ToastTone = ToastTone.INFO, val createdAtEpoch: Long = System.currentTimeMillis() / 1000)
enum class ToastTone { INFO, SUCCESS, WARNING, ERROR }

class MusGoViewModel : ViewModel() {
    private val _state = MutableStateFlow(MusGoUiState())
    val state: StateFlow<MusGoUiState> = _state.asStateFlow()

    // =========================================================
    // CHAT
    // =========================================================

    fun setActiveChatSession(id: String) = _state.update { it.copy(activeChatSessionId = id) }

    fun createChatSession(title: String, projectId: String? = null): String {
        val id = "c-${UUID.randomUUID()}"
        val now = System.currentTimeMillis() / 1000
        val s = ChatSession(
            id = id, title = title, projectId = projectId,
            providerId = _state.value.selectedProviderId, modelId = _state.value.selectedModelId,
            routingMode = _state.value.activeRoutingMode, messageCount = 0, tokensUsed = 0,
            updatedAtEpoch = now, createdAtEpoch = now,
        )
        _state.update { it.copy(chatSessions = listOf(s) + it.chatSessions, chatMessagesBySession = it.chatMessagesBySession + (id to emptyList()), activeChatSessionId = id) }
        toast("Session \"$title\" created")
        return id
    }

    fun deleteChatSession(id: String) {
        _state.update { s ->
            val remaining = s.chatSessions.filterNot { it.id == id }
            val next = remaining.firstOrNull()?.id ?: ""
            val active = if (s.activeChatSessionId == id) next else s.activeChatSessionId
            s.copy(
                chatSessions = remaining,
                chatMessagesBySession = s.chatMessagesBySession - id,
                activeChatSessionId = active,
            )
        }
        toast("Session deleted", ToastTone.WARNING)
    }

    fun appendMessage(sessionId: String, role: MessageRole, content: String, provider: String? = null, model: String? = null, attachments: List<ChatAttachment> = emptyList()) {
        val msg = ChatMessage(
            id = "cm-${UUID.randomUUID()}",
            sessionId = sessionId,
            role = role,
            content = content,
            timestampEpoch = System.currentTimeMillis() / 1000,
            providerUsed = provider,
            modelUsed = model,
            attachments = attachments,
            status = MessageStatus.OK,
        )
        _state.update { s ->
            val list = s.chatMessagesBySession[sessionId].orEmpty()
            s.copy(chatMessagesBySession = s.chatMessagesBySession + (sessionId to (list + msg)))
        }
    }

    fun sendUserMessage(sessionId: String, content: String, attachments: List<ChatAttachment> = emptyList()) {
        appendMessage(sessionId, MessageRole.USER, content.trim(), attachments = attachments)
        val providerName = providerById(_state.value.selectedProviderId)?.name ?: "Router"
        appendMessage(sessionId, MessageRole.ASSISTANT, simulateResponse(content), provider = providerName, model = _state.value.selectedModelId)
        rotateActiveKeyIfNeeded()
    }

    fun editMessage(sessionId: String, messageId: String, newContent: String) {
        _state.update { s ->
            val list = s.chatMessagesBySession[sessionId].orEmpty().map {
                if (it.id == messageId) it.copy(content = newContent, edited = true, editedAtEpoch = System.currentTimeMillis() / 1000)
                else it
            }
            s.copy(chatMessagesBySession = s.chatMessagesBySession + (sessionId to list))
        }
        toast("Message edited")
    }

    fun resendMessage(sessionId: String, messageId: String) {
        val original = _state.value.chatMessagesBySession[sessionId]?.firstOrNull { it.id == messageId } ?: return
        appendMessage(sessionId, MessageRole.USER, original.content, attachments = original.attachments)
        val providerName = providerById(_state.value.selectedProviderId)?.name ?: "Router"
        appendMessage(sessionId, MessageRole.ASSISTANT, simulateResponse(original.content), provider = providerName, model = _state.value.selectedModelId)
        toast("Message resent", ToastTone.SUCCESS)
    }

    fun toggleBookmark(sessionId: String, messageId: String) {
        _state.update { s ->
            val list = s.chatMessagesBySession[sessionId].orEmpty().map {
                if (it.id == messageId) it.copy(bookmarked = !it.bookmarked) else it
            }
            val session = s.chatSessions.firstOrNull { it.id == sessionId }
            val updatedSession = session?.copy(
                bookmarkedMessageIds = if (session.bookmarkedMessageIds.contains(messageId))
                    session.bookmarkedMessageIds - messageId
                else session.bookmarkedMessageIds + messageId,
            )
            s.copy(
                chatMessagesBySession = s.chatMessagesBySession + (sessionId to list),
                chatSessions = s.chatSessions.map { if (it.id == sessionId) updatedSession ?: it else it },
            )
        }
    }

    fun regenerateAssistantMessage(sessionId: String, assistantMessageId: String) {
        val list = _state.value.chatMessagesBySession[sessionId].orEmpty()
        val idx = list.indexOfFirst { it.id == assistantMessageId }
        if (idx <= 0) return
        val prevUser = list[idx - 1]
        val providerName = providerById(_state.value.selectedProviderId)?.name ?: "Router"
        _state.update { s ->
            val newList = list.toMutableList()
            newList[idx] = list[idx].copy(
                content = simulateResponse(prevUser.content) + "\n\n— regenerated #${(list[idx].regenerated + 1)} —",
                regenerated = list[idx].regenerated + 1,
            )
            s.copy(chatMessagesBySession = s.chatMessagesBySession + (sessionId to newList))
        }
        toast("Regenerated", ToastTone.SUCCESS)
    }

    fun addAttachment(sessionId: String, attachment: ChatAttachment) {
        val list = _state.value.chatMessagesBySession[sessionId].orEmpty().toMutableList()
        if (list.isEmpty()) {
            appendMessage(sessionId, MessageRole.USER, "", attachments = listOf(attachment))
        } else {
            val last = list.last()
            val updated = last.copy(attachments = last.attachments + attachment)
            list[list.size - 1] = updated
            _state.update { it.copy(chatMessagesBySession = it.chatMessagesBySession + (sessionId to list)) }
        }
        toast("Attached ${attachment.name}")
    }

    private fun simulateResponse(prompt: String): String {
        val lower = prompt.lowercase()
        return when {
            "rbac" in lower || "role" in lower -> "I'll handle this via the agent chain:\n• Planner decomposes into steps\n• Developer applies diff-aware edits to src/auth/Rbac.kt\n• Tester runs JUnit5 spec (sk-tester-1)\n• Builder produces APK\n• Reviewer + human approval gate\nRouting: balanced. Context prepared from bp-android + sk-coder-1."
            "build" in lower -> "Triggering forge-agent with the cached gradle wrapper. Sandbox network is closed; dependencies are local. Expected output: app-debug.apk at /tmp/sandboxes/pr-1/."
            "deploy" in lower -> "atlas-agent is staging the artifact (checksum sha256:…e2). Awaiting approval before promoting to canary. Health gate will probe /healthz."
            "plan" in lower || "task" in lower -> "helios-agent decomposing goal:\n1. Inspect existing structure\n2. Identify dependencies\n3. Sequence steps with timeouts\n4. Allocate agents by capability\nPersist to workflow memory."
            else -> "Routing via Router (${_state.value.activeRoutingMode.name.lowercase()}). I'll prepare context from the active project, match a skill, and dispatch to the orchestrator. No secrets exposed to logs or context."
        }
    }

    // =========================================================
    // PROVIDER / API KEYS
    // =========================================================

    fun setRoutingMode(mode: RoutingMode) = _state.update { it.copy(activeRoutingMode = mode) }
    fun setProvider(id: String) = _state.update { it.copy(selectedProviderId = id) }
    fun setModel(id: String) = _state.update { it.copy(selectedModelId = id) }

    fun createProvider(name: String, type: ProviderType, baseUrl: String, region: String): String {
        val id = "p-${UUID.randomUUID()}"
        val provider = AIProvider(
            id = id, name = name, region = region, type = type, baseUrl = baseUrl,
            status = ProviderStatus.UNKNOWN, latencyMs = 0, models = emptyList(),
            supportsVision = true, supportsTools = true, monthlyUsagePct = 0,
            priority = 80,
        )
        _state.update { it.copy(providers = it.providers + provider) }
        toast("Provider \"$name\" created", ToastTone.SUCCESS)
        return id
    }

    fun deleteProvider(providerId: String) {
        _state.update { s ->
            val p = s.providers.firstOrNull { it.id == providerId }
            val totalKeys = p?.apiKeys?.size ?: 0
            s.copy(providers = s.providers.filterNot { it.id == providerId })
                .also { toast("Provider removed · $totalKeys keys revoked", ToastTone.WARNING) }
        }
    }

    fun addProviderKey(providerId: String, label: String, rawToken: String, priority: Int): String {
        val id = "k-${UUID.randomUUID()}"
        val masked = maskToken(rawToken, providerType(_state.value, providerId))
        val now = System.currentTimeMillis() / 1000
        val key = ProviderApiKey(
            id = id, label = label, maskedToken = masked, priority = priority,
            addedAtEpoch = now, lastUsedEpoch = 0, lastTestEpoch = 0, lastTestOk = false,
        )
        _state.update { s ->
            s.copy(providers = s.providers.map {
                if (it.id != providerId) it
                else it.copy(
                    apiKeys = it.apiKeys + key,
                    activeKeyId = it.activeKeyId ?: id,
                    status = if (it.status == ProviderStatus.UNKNOWN) ProviderStatus.ONLINE else it.status,
                )
            })
        }
        toast("API key added · $label", ToastTone.SUCCESS)
        return id
    }

    fun removeProviderKey(providerId: String, keyId: String) {
        _state.update { s ->
            s.copy(providers = s.providers.map {
                if (it.id != providerId) it
                else it.copy(
                    apiKeys = it.apiKeys.filterNot { k -> k.id == keyId },
                    activeKeyId = if (it.activeKeyId == keyId) it.apiKeys.firstOrNull { k -> k.id != keyId }?.id else it.activeKeyId,
                )
            })
        }
        toast("Key removed", ToastTone.WARNING)
    }

    fun toggleProviderKey(providerId: String, keyId: String, enabled: Boolean) {
        _state.update { s ->
            s.copy(providers = s.providers.map {
                if (it.id != providerId) it
                else it.copy(apiKeys = it.apiKeys.map { k -> if (k.id == keyId) k.copy(enabled = enabled) else k })
            })
        }
    }

    fun setActiveProviderKey(providerId: String, keyId: String) {
        _state.update { s ->
            s.copy(providers = s.providers.map { if (it.id != providerId) it else it.copy(activeKeyId = keyId) })
        }
        toast("Active key rotated")
    }

    fun setKeyPriority(providerId: String, keyId: String, priority: Int) {
        _state.update { s ->
            s.copy(providers = s.providers.map {
                if (it.id != providerId) it
                else it.copy(apiKeys = it.apiKeys.map { k -> if (k.id == keyId) k.copy(priority = priority) else k })
            })
        }
    }

    fun setProviderPriority(providerId: String, priority: Int) {
        _state.update { s ->
            s.copy(providers = s.providers.map { if (it.id != providerId) it else it.copy(priority = priority) })
        }
    }

    fun testProviderKey(providerId: String, keyId: String) {
        _state.update { s ->
            s.copy(providers = s.providers.map {
                if (it.id != providerId) it
                else it.copy(apiKeys = it.apiKeys.map { k ->
                    if (k.id != keyId) k
                    else k.copy(
                        lastTestEpoch = System.currentTimeMillis() / 1000,
                        lastTestOk = true,
                        status = ProviderStatus.ONLINE,
                    )
                })
            })
        }
        toast("Key tested OK", ToastTone.SUCCESS)
    }

    fun toggleSkill(skillId: String) = _state.update { s ->
        s.copy(skills = s.skills.map { if (it.id == skillId) it.copy(enabled = !it.enabled) else it })
    }

    fun toggleModel(providerId: String, modelId: String) = _state.update { s ->
        s.copy(providers = s.providers.map { p ->
            if (p.id != providerId) p else p.copy(models = p.models.map { m -> if (m.id == modelId) m.copy(enabled = !m.enabled) else m })
        })
    }

    fun rotateActiveKeyIfNeeded() {
        _state.update { s ->
            s.copy(providers = s.providers.map { p ->
                val active = p.apiKeys.firstOrNull { it.id == p.activeKeyId }
                if (active != null) {
                    val now = System.currentTimeMillis() / 1000
                    p.copy(apiKeys = p.apiKeys.map { if (it.id == active.id) it.copy(lastUsedEpoch = now, requestsToday = it.requestsToday + 1) else it })
                } else p
            })
        }
    }

    // =========================================================
    // WORKFLOW CRUD
    // =========================================================

    fun createWorkflow(name: String, projectId: String, requiresHumanApproval: Boolean): String {
        val id = "wf-${UUID.randomUUID()}"
        val now = System.currentTimeMillis() / 1000
        val plan = WorkflowStep("s-plan", 0, "Plan", StepKind.PLAN, StepState.PENDING, null, emptyList(), 600, null, null)
        val wf = Workflow(id = id, projectId = projectId, name = name, state = WorkflowState.DRAFT,
            currentStepIndex = 0, createdAtEpoch = now, updatedAtEpoch = now,
            steps = listOf(plan), requiresHumanApproval = requiresHumanApproval)
        _state.update { it.copy(workflows = it.workflows + wf) }
        toast("Workflow \"$name\" created", ToastTone.SUCCESS)
        return id
    }

    fun deleteWorkflow(workflowId: String) {
        _state.update { it.copy(workflows = it.workflows.filterNot { wf -> wf.id == workflowId }) }
        toast("Workflow deleted", ToastTone.WARNING)
    }

    fun addWorkflowStep(workflowId: String, name: String, kind: StepKind) {
        _state.update { s ->
            s.copy(workflows = s.workflows.map { wf ->
                if (wf.id != workflowId) wf
                else wf.copy(
                    steps = wf.steps + WorkflowStep(
                        id = "s-${UUID.randomUUID()}",
                        order = wf.steps.size,
                        name = name,
                        kind = kind,
                        state = StepState.PENDING,
                        agentId = null,
                        dependsOn = if (wf.steps.isNotEmpty()) listOf(wf.steps.last().id) else emptyList(),
                        timeoutSec = 600,
                        startedAtEpoch = null,
                        completedAtEpoch = null,
                    ),
                    updatedAtEpoch = System.currentTimeMillis() / 1000,
                )
            })
        }
    }

    fun removeWorkflowStep(workflowId: String, stepId: String) {
        _state.update { s ->
            s.copy(workflows = s.workflows.map { wf ->
                if (wf.id != workflowId) wf
                else wf.copy(
                    steps = wf.steps.filterNot { it.id == stepId }.mapIndexed { idx, st -> st.copy(order = idx) },
                    updatedAtEpoch = System.currentTimeMillis() / 1000,
                )
            })
        }
    }

    fun runWorkflow(workflowId: String) {
        _state.update { s ->
            s.copy(workflows = s.workflows.map { wf ->
                if (wf.id != workflowId) wf
                else wf.copy(
                    state = WorkflowState.RUNNING,
                    currentStepIndex = 0,
                    steps = wf.steps.mapIndexed { idx, st -> if (idx == 0) st.copy(state = StepState.RUNNING, startedAtEpoch = System.currentTimeMillis() / 1000) else st.copy(state = StepState.PENDING) },
                    updatedAtEpoch = System.currentTimeMillis() / 1000,
                )
            })
        }
        toast("Workflow started", ToastTone.SUCCESS)
    }

    fun approveDeployment(deploymentId: String) = _state.update { s ->
        s.copy(deployments = s.deployments.map { if (it.id == deploymentId) it.copy(state = com.agon.app.data.DeployState.IN_PROGRESS) else it })
    }

    fun advanceWorkflow(workflowId: String) = _state.update { s ->
        s.copy(workflows = s.workflows.map { wf ->
            if (wf.id != workflowId) wf else wf.copy(currentStepIndex = wf.currentStepIndex + 1, updatedAtEpoch = System.currentTimeMillis() / 1000)
        })
    }

    // =========================================================
    // SANDBOX LIFECYCLE
    // =========================================================

    fun createSandbox(projectId: String, runtime: SandboxRuntime = SandboxRuntime.LOCAL_SHELL): String {
        val id = "sb-${UUID.randomUUID()}"
        val now = System.currentTimeMillis() / 1000
        val sb = Sandbox(
            id = id, projectId = projectId,
            state = com.agon.app.data.SandboxState.PROVISIONING,
            workdirPath = "/tmp/sandboxes/$id",
            createdAtEpoch = now, expiresAtEpoch = now + 24 * 3600,
            networkAccess = false, resourceLimitMb = 4096, cpuLimit = "2 cores",
            commandCount = 0, logCount = 0, clonedSha = null,
            runtime = runtime,
        )
        _state.update { it.copy(sandboxes = it.sandboxes + sb) }
        toast("Sandbox provisioned", ToastTone.SUCCESS)
        return id
    }

    fun destroySandbox(sandboxId: String) {
        _state.update { s ->
            s.copy(sandboxes = s.sandboxes.map { if (it.id == sandboxId) it.copy(state = com.agon.app.data.SandboxState.DESTROYED) else it })
        }
        toast("Sandbox destroyed · persistent state preserved", ToastTone.WARNING)
    }

    fun executeSandboxCommand(sandboxId: String, command: String) {
        val now = System.currentTimeMillis() / 1000
        val log = SandboxLog(
            id = "l-${UUID.randomUUID()}",
            sandboxId = sandboxId,
            stream = com.agon.app.data.LogStream.STDOUT,
            command = command,
            line = "[musgo] executed '$command' (sandbox-local)",
            timestampEpoch = now,
            exitCode = 0,
        )
        _state.update { s ->
            s.copy(
                sandboxLogs = s.sandboxLogs + log,
                sandboxes = s.sandboxes.map { sb ->
                    if (sb.id != sandboxId) sb
                    else sb.copy(
                        commandCount = sb.commandCount + 1,
                        logCount = sb.logCount + 1,
                        state = if (sb.state == com.agon.app.data.SandboxState.READY) com.agon.app.data.SandboxState.EXECUTING else sb.state,
                    )
                },
            )
        }
        toast("Command executed", ToastTone.SUCCESS)
    }

    // =========================================================
    // TERMINAL
    // =========================================================

    fun connectTerminal(terminalId: String) {
        _state.update { s ->
            s.copy(terminals = s.terminals.map { if (it.id == terminalId) it.copy(state = TerminalState.CONNECTED) else it })
        }
        toast("Terminal connected", ToastTone.SUCCESS)
    }

    fun disconnectTerminal(terminalId: String) {
        _state.update { s ->
            s.copy(terminals = s.terminals.map { if (it.id == terminalId) it.copy(state = TerminalState.DISCONNECTED) else it })
        }
        toast("Terminal disconnected", ToastTone.WARNING)
    }

    // =========================================================
    // LIBRARY (simplified ingestion)
    // =========================================================

    fun ingestBlueprintFromUrl(title: String, sourceUrl: String) {
        val bp = Blueprint(
            id = "bp-${UUID.randomUUID()}", title = title, framework = "from-url", category = "imported",
            summary = "Imported from $sourceUrl · index built at ingestion", indexedChunks = 12,
            trusted = false, stars = 0, uses = 0, tags = listOf("imported", "url"),
        )
        _state.update { it.copy(blueprints = listOf(bp) + it.blueprints) }
        toast("Blueprint \"$title\" ingested", ToastTone.SUCCESS)
    }

    fun ingestSkillFromUrl(name: String, sourceUrl: String) {
        val sk = Skill(
            id = "sk-${UUID.randomUUID()}", name = name, version = "0.1.0",
            source = com.agon.app.data.SkillSource.URL, category = "imported",
            description = "Imported from $sourceUrl", enabled = true, trusted = false,
            sizeKb = 4, tags = listOf("imported", "url"), usageCount = 0,
        )
        _state.update { it.copy(skills = listOf(sk) + it.skills) }
        toast("Skill \"$name\" ingested", ToastTone.SUCCESS)
    }

    // =========================================================
    // GITHUB PAT
    // =========================================================

    fun revokePat(patId: String) = _state.update { s ->
        s.copy(pats = s.pats.map { if (it.id == patId) it.copy(status = com.agon.app.data.PatStatus.REVOKED) else it })
    }

    fun addGitHubPat(label: String, rawToken: String): String {
        val id = "pat-${UUID.randomUUID()}"
        val masked = maskGithubToken(rawToken)
        val pat = GitHubPat(id = id, label = label, maskedToken = masked,
            scopes = listOf("repo", "read:org"), addedAtEpoch = System.currentTimeMillis() / 1000,
            lastUsedEpoch = 0, status = com.agon.app.data.PatStatus.ACTIVE)
        _state.update { it.copy(pats = listOf(pat) + it.pats) }
        toast("PAT added · $label", ToastTone.SUCCESS)
        return id
    }

    // =========================================================
    // TOASTS
    // =========================================================

    fun dismissToast(id: String) {
        _state.update { it.copy(toasts = it.toasts.filterNot { t -> t.id == id }) }
    }

    private fun toast(message: String, tone: ToastTone = ToastTone.INFO) {
        val t = ToastEvent(id = "toast-${UUID.randomUUID()}", message = message, tone = tone)
        _state.update { it.copy(toasts = it.toasts + t) }
    }

    // =========================================================
    // LOOKUPS / DERIVED
    // =========================================================

    fun projectById(id: String?): Project? = _state.value.projects.firstOrNull { it.id == id }
    fun providerById(id: String?): AIProvider? = _state.value.providers.firstOrNull { it.id == id }
    fun blueprintById(id: String?): Blueprint? = _state.value.blueprints.firstOrNull { it.id == id }
    fun skillById(id: String?): Skill? = _state.value.skills.firstOrNull { it.id == id }
    fun repoById(id: String?): GitHubRepo? = _state.value.repos.firstOrNull { it.id == id }
    fun patById(id: String?): GitHubPat? = _state.value.pats.firstOrNull { it.id == id }
    fun agentById(id: String?): Agent? = _state.value.agents.firstOrNull { it.id == id }
    fun sandboxById(id: String?): Sandbox? = _state.value.sandboxes.firstOrNull { it.id == id }
    fun workflowById(id: String?): Workflow? = _state.value.workflows.firstOrNull { it.id == id }
    fun terminalById(id: String?): TerminalSession? = _state.value.terminals.firstOrNull { it.id == id }

    fun totalTokens(): Long = _state.value.chatSessions.sumOf { it.tokensUsed }
    fun onlineProviderCount(): Int = _state.value.providers.count { it.status == ProviderStatus.ONLINE }
    fun activeProjectsCount(): Int = _state.value.projects.count { it.status != com.agon.app.data.ProjectStatus.DRAFT && it.status != com.agon.app.data.ProjectStatus.PAUSED }
    fun runningTasksCount(): Int = _state.value.tasks.count { it.state == com.agon.app.data.TaskState.RUNNING }
    fun runningWorkflowsCount(): Int = _state.value.workflows.count { it.state == WorkflowState.RUNNING || it.state == WorkflowState.AWAITING_APPROVAL }
    fun liveDeploys(): Int = _state.value.deployments.count { it.state == com.agon.app.data.DeployState.LIVE }

    // =========================================================
    // Helpers
    // =========================================================

    private fun maskToken(raw: String, type: ProviderType): String {
        if (raw.length <= 8) return "••••"
        return when (type) {
            ProviderType.OPENAI_COMPATIBLE -> "sk-${raw.take(3)}••••••••••••${raw.takeLast(4)}"
            ProviderType.ANTHROPIC_COMPATIBLE -> "sk-ant-${raw.take(3)}••••••••••••${raw.takeLast(4)}"
            ProviderType.GOOGLE_COMPATIBLE -> "AIza••••••••••••${raw.takeLast(4)}"
            ProviderType.LOCAL_OLLAMA -> "local-${raw.take(3)}"
            ProviderType.CUSTOM_HTTP -> "sov-${raw.take(3)}••••••••••••${raw.takeLast(4)}"
        }
    }

    private fun maskGithubToken(raw: String): String {
        if (raw.length <= 8) return "ghp_••••"
        return "ghp_••••••••••••${raw.takeLast(4)}"
    }

    private fun providerType(s: MusGoUiState, providerId: String): ProviderType =
        s.providers.firstOrNull { it.id == providerId }?.type ?: ProviderType.CUSTOM_HTTP
}
