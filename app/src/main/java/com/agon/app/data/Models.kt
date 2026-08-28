package com.agon.app.data

import kotlinx.serialization.Serializable

// =========================================================
// FOUNDATION MODELS
// =========================================================

@Serializable
data class UserProfile(
    val id: String,
    val displayName: String,
    val handle: String,
    val email: String,
    val role: String,
    val org: String,
    val avatarSeed: String,
    val verified: Boolean = true,
)

// =========================================================
// AI PROVIDER
// =========================================================

@Serializable
data class AIProvider(
    val id: String,
    val name: String,
    val region: String,
    val type: ProviderType,
    val baseUrl: String = "",
    val status: ProviderStatus,
    val latencyMs: Int,
    val models: List<AIModel>,
    val supportsVision: Boolean,
    val supportsTools: Boolean,
    val monthlyUsagePct: Int,
    val priority: Int = 100,
    val apiKeys: List<ProviderApiKey> = emptyList(),
    val activeKeyId: String? = null,
)

@Serializable
data class ProviderApiKey(
    val id: String,
    val label: String,
    val maskedToken: String,
    val priority: Int = 50,
    val enabled: Boolean = true,
    val status: ProviderStatus = ProviderStatus.ONLINE,
    val addedAtEpoch: Long,
    val lastUsedEpoch: Long = 0,
    val lastTestEpoch: Long = 0,
    val lastTestOk: Boolean = false,
    val requestsToday: Int = 0,
    val failuresToday: Int = 0,
    val cooldownUntilEpoch: Long = 0,
    val notes: String = "",
)

@Serializable
enum class ProviderType { OPENAI_COMPATIBLE, ANTHROPIC_COMPATIBLE, GOOGLE_COMPATIBLE, LOCAL_OLLAMA, CUSTOM_HTTP }
@Serializable
enum class ProviderStatus { ONLINE, DEGRADED, OFFLINE, MAINTENANCE, RATE_LIMITED, COOLDOWN, UNKNOWN }

@Serializable
data class AIModel(
    val id: String,
    val name: String,
    val contextWindow: Int,
    val capability: ModelCapability,
    val costPer1kTokens: Double,
    val enabled: Boolean,
)

@Serializable
enum class ModelCapability { CHAT, CODE, REASONING, VISION, EMBEDDING, LONG_CONTEXT, AGENT }

@Serializable
enum class RoutingMode { AUTO_FREE, AUTO_BALANCED, AUTO_QUICK, MANUAL }

// =========================================================
// CHAT
// =========================================================

@Serializable
data class ChatSession(
    val id: String,
    val title: String,
    val projectId: String?,
    val providerId: String,
    val modelId: String,
    val routingMode: RoutingMode,
    val messageCount: Int,
    val tokensUsed: Long,
    val updatedAtEpoch: Long,
    val pinned: Boolean = false,
    val bookmarkedMessageIds: List<String> = emptyList(),
    val createdAtEpoch: Long = updatedAtEpoch,
    val archived: Boolean = false,
)

@Serializable
data class ChatAttachment(
    val id: String,
    val name: String,
    val mimeType: String,
    val sizeBytes: Long,
    val localUri: String? = null,
    val source: AttachmentSource,
    val addedAtEpoch: Long,
)

@Serializable
enum class AttachmentSource { CLIPBOARD, FILE_PICKER, CAMERA, VOICE_NOTE, URL }

@Serializable
data class ChatMessage(
    val id: String,
    val sessionId: String,
    val role: MessageRole,
    val content: String,
    val timestampEpoch: Long,
    val tokensIn: Int = 0,
    val tokensOut: Int = 0,
    val providerUsed: String? = null,
    val modelUsed: String? = null,
    val attachments: List<ChatAttachment> = emptyList(),
    val bookmarked: Boolean = false,
    val edited: Boolean = false,
    val editedAtEpoch: Long = 0,
    val regenerated: Int = 0,
    val parentMessageId: String? = null,
    val status: MessageStatus = MessageStatus.OK,
)

@Serializable
enum class MessageStatus { OK, STREAMING, FAILED, REDACTED }

@Serializable
enum class MessageRole { USER, ASSISTANT, SYSTEM, TOOL, AGENT_PLANNER, AGENT_DEVELOPER, AGENT_TESTER, AGENT_BUILDER, AGENT_DEPLOY }

// =========================================================
// PROJECT
// =========================================================

@Serializable
data class Project(
    val id: String,
    val name: String,
    val slug: String,
    val description: String,
    val status: ProjectStatus,
    val repoFullName: String?,
    val branch: String,
    val visibility: ProjectVisibility,
    val tags: List<String>,
    val createdAtEpoch: Long,
    val lastBuildAtEpoch: Long?,
    val lastDeployAtEpoch: Long?,
    val agentCount: Int,
    val skillCount: Int,
    val blueprintCount: Int,
    val workflowStepCount: Int,
)

@Serializable
enum class ProjectStatus { DRAFT, ACTIVE, BUILDING, TESTING, AWAITING_APPROVAL, DEPLOYED, FAILED, PAUSED }
@Serializable
enum class ProjectVisibility { PRIVATE, INTERNAL, ORG, PUBLIC }

// =========================================================
// AGENT ORCHESTRATOR
// =========================================================

@Serializable
data class Agent(
    val id: String,
    val name: String,
    val role: AgentRole,
    val description: String,
    val state: AgentState,
    val activeTaskId: String?,
    val modelPreference: String,
    val capabilities: List<String>,
    val skillIds: List<String>,
    val blueprintIds: List<String>,
    val successRate: Double,
    val tasksCompleted: Int,
    val avgLatencyMs: Int,
)

@Serializable
enum class AgentRole { PLANNER, DEVELOPER, TESTER, BUILDER, DEPLOYER, REVIEWER, SECURITY, DOCS }
@Serializable
enum class AgentState { IDLE, RUNNING, WAITING_APPROVAL, AWAITING_HUMAN, RECOVERING, OFFLINE }

@Serializable
data class AgentTask(
    val id: String,
    val title: String,
    val projectId: String,
    val agentId: String,
    val state: TaskState,
    val progressPct: Int,
    val startedAtEpoch: Long,
    val completedAtEpoch: Long? = null,
    val dependencies: List<String>,
    val artifacts: List<String>,
    val retries: Int,
    val blockedBy: String? = null,
)

@Serializable
enum class TaskState { QUEUED, RUNNING, WAITING, REVIEW, DONE, FAILED, BLOCKED, CANCELLED }

// =========================================================
// SKILL
// =========================================================

@Serializable
data class Skill(
    val id: String,
    val name: String,
    val version: String,
    val source: SkillSource,
    val category: String,
    val description: String,
    val enabled: Boolean,
    val trusted: Boolean,
    val sizeKb: Int,
    val tags: List<String>,
    val usageCount: Int,
)

@Serializable
enum class SkillSource { UPLOAD, PASTED, GITHUB, URL, AGENT_CREATED, ZIP }

// =========================================================
// BLUEPRINT LIBRARY
// =========================================================

@Serializable
data class Blueprint(
    val id: String,
    val title: String,
    val framework: String,
    val category: String,
    val summary: String,
    val indexedChunks: Int,
    val trusted: Boolean,
    val stars: Int,
    val uses: Int,
    val tags: List<String>,
)

// =========================================================
// GITHUB
// =========================================================

@Serializable
data class GitHubPat(
    val id: String,
    val label: String,
    val maskedToken: String,
    val scopes: List<String>,
    val addedAtEpoch: Long,
    val lastUsedEpoch: Long,
    val status: PatStatus,
)

@Serializable
enum class PatStatus { ACTIVE, EXPIRING, REVOKED }

@Serializable
data class GitHubRepo(
    val id: String,
    val fullName: String,
    val branch: String,
    val visibility: ProjectVisibility,
    val defaultBranch: String,
    val openPrs: Int,
    val openIssues: Int,
    val lastCommitSha: String,
    val lastCommitMessage: String,
    val sizeKb: Int,
    val connectedProjectId: String?,
)

@Serializable
data class PullRequest(
    val id: String,
    val number: Int,
    val repoFullName: String,
    val title: String,
    val state: PrState,
    val author: String,
    val sourceBranch: String,
    val targetBranch: String,
    val checksPassing: Boolean,
    val additions: Int,
    val deletions: Int,
    val createdAtEpoch: Long,
    val projectId: String?,
)

@Serializable
enum class PrState { OPEN, MERGED, CLOSED, DRAFT }

// =========================================================
// SANDBOX
// =========================================================

@Serializable
data class Sandbox(
    val id: String,
    val projectId: String,
    val state: SandboxState,
    val workdirPath: String,
    val createdAtEpoch: Long,
    val expiresAtEpoch: Long,
    val networkAccess: Boolean,
    val resourceLimitMb: Int,
    val cpuLimit: String,
    val commandCount: Int,
    val logCount: Int,
    val clonedSha: String?,
    val runtime: SandboxRuntime = SandboxRuntime.LOCAL_SHELL,
    val remoteEndpoint: String? = null,
)

@Serializable
enum class SandboxRuntime { LOCAL_SHELL, REMOTE_DOCKER, REMOTE_K8S, REMOTE_SSH }

@Serializable
data class TerminalSession(
    val id: String,
    val sandboxId: String,
    val kind: TerminalKind,
    val state: TerminalState,
    val startedAtEpoch: Long,
    val lastCommandAtEpoch: Long = 0,
    val commandCount: Int = 0,
    val endpoint: String,
    val requiresRemote: Boolean,
)

@Serializable
enum class TerminalKind { LOCAL_SHELL, SSH, MCP, DOCKER_EXEC, K8S_EXEC }
@Serializable
enum class TerminalState { CONNECTED, DISCONNECTED, ERROR, IDLE }

@Serializable
enum class SandboxState { PROVISIONING, READY, EXECUTING, AWAITING_APPROVAL, DESTROYED, FAILED }

@Serializable
data class SandboxLog(
    val id: String,
    val sandboxId: String,
    val stream: LogStream,
    val command: String,
    val line: String,
    val timestampEpoch: Long,
    val exitCode: Int?,
)

@Serializable
enum class LogStream { STDOUT, STDERR, SYSTEM, APPROVAL, NETWORK }

// =========================================================
// WORKFLOW
// =========================================================

@Serializable
data class Workflow(
    val id: String,
    val projectId: String,
    val name: String,
    val state: WorkflowState,
    val currentStepIndex: Int,
    val createdAtEpoch: Long,
    val updatedAtEpoch: Long,
    val steps: List<WorkflowStep>,
    val requiresHumanApproval: Boolean,
)

@Serializable
enum class WorkflowState { DRAFT, RUNNING, PAUSED, AWAITING_APPROVAL, COMPLETED, FAILED, ROLLED_BACK }

@Serializable
data class WorkflowStep(
    val id: String,
    val order: Int,
    val name: String,
    val kind: StepKind,
    val state: StepState,
    val agentId: String?,
    val dependsOn: List<String>,
    val timeoutSec: Int,
    val startedAtEpoch: Long?,
    val completedAtEpoch: Long?,
    val errorMessage: String? = null,
    val parallel: Boolean = false,
)

@Serializable
enum class StepKind { PLAN, CODE, TEST, BUILD, COMMIT, PUSH, PR, REVIEW, APPROVAL, DEPLOY, HEALTHCHECK }
@Serializable
enum class StepState { PENDING, RUNNING, DONE, FAILED, SKIPPED, BLOCKED, WAITING_APPROVAL }

// =========================================================
// MEMORY
// =========================================================

@Serializable
data class MemoryEntry(
    val id: String,
    val scope: MemoryScope,
    val scopeId: String,
    val title: String,
    val body: String,
    val tags: List<String>,
    val createdAtEpoch: Long,
    val retentionDays: Int,
    val pinned: Boolean,
)

@Serializable
enum class MemoryScope { USER, PROJECT, TASK, AGENT, WORKFLOW, HANDOVER }

// =========================================================
// HANDOVER
// =========================================================

@Serializable
data class Handover(
    val id: String,
    val fromAgentId: String,
    val toAgentId: String,
    val projectId: String,
    val task: String,
    val currentState: String,
    val completedWork: List<String>,
    val remainingWork: List<String>,
    val nextAction: String,
    val filesInvolved: List<String>,
    val dependencies: List<String>,
    val blockers: List<String>,
    val decisions: List<String>,
    val skillsActive: List<String>,
    val blueprintsActive: List<String>,
    val repoSha: String?,
    val buildArtifactId: String?,
    val createdAtEpoch: Long,
    val resumedByAgentId: String? = null,
)

// =========================================================
// BUILD / ARTIFACT
// =========================================================

@Serializable
data class BuildArtifact(
    val id: String,
    val projectId: String,
    val version: String,
    val commitSha: String,
    val type: ArtifactType,
    val sizeMb: Double,
    val checksum: String,
    val producedAtEpoch: Long,
    val deployTarget: String?,
    val status: ArtifactStatus,
)

@Serializable
enum class ArtifactType { APK, AAB, JAR, IMAGE, BUNDLE, DOCS, COMPILED_BIN }
@Serializable
enum class ArtifactStatus { PENDING, VERIFIED, DEPLOYED, SUPERSEDED, FAILED }

// =========================================================
// DEPLOYMENT
// =========================================================

@Serializable
data class Deployment(
    val id: String,
    val projectId: String,
    val artifactId: String,
    val environment: DeployEnvironment,
    val state: DeployState,
    val version: String,
    val url: String?,
    val startedAtEpoch: Long,
    val completedAtEpoch: Long?,
    val healthCheckPass: Boolean,
    val rollbackAvailable: Boolean,
    val notes: String,
)

@Serializable
enum class DeployEnvironment { STAGING, CANARY, PRODUCTION, EDGE }
@Serializable
enum class DeployState { QUEUED, IN_PROGRESS, LIVE, DEGRADED, FAILED, ROLLED_BACK }

// =========================================================
// SECURITY
// =========================================================

@Serializable
data class AuditEvent(
    val id: String,
    val timestampEpoch: Long,
    val actor: String,
    val action: String,
    val target: String,
    val severity: AuditSeverity,
    val outcome: String,
)

@Serializable
enum class AuditSeverity { INFO, NOTICE, WARNING, CRITICAL }

@Serializable
data class SecurityPosture(
    val secretCount: Int,
    val patCount: Int,
    val oauthCount: Int,
    val sandboxEgressAllowed: Boolean,
    val dangerousActionConfirm: Boolean,
    val redactionEnabled: Boolean,
    val lastScanEpoch: Long?,
    val threats: List<String>,
)
