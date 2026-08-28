package com.agon.app.data

object SampleData {
    private const val HOUR = 3600L
    private const val DAY = 86400L
    private const val NOW = 1_730_000_000L // fixed epoch so UI is stable

    fun now(): Long = NOW

    val user = UserProfile(
        id = "u-001",
        displayName = "Dhani Yuliawan",
        handle = "@dhani",
        email = "dhani@musgo.sovereign",
        role = "Sovereign Architect",
        org = "MusGo-OS Foundation",
        avatarSeed = "DY",
    )

    val providers = listOf(
        AIProvider(
            id = "p-1", name = "OpenAI-Compatible Edge", region = "Global / SG-1", type = ProviderType.OPENAI_COMPATIBLE,
            baseUrl = "https://api.openai.com/v1", priority = 100,
            status = ProviderStatus.ONLINE, latencyMs = 320,
            supportsVision = true, supportsTools = true, monthlyUsagePct = 42,
            apiKeys = listOf(
                ProviderApiKey("k-1a", "Production · prod-edge", "sk-••••••••••••3f2a", priority = 90,
                    addedAtEpoch = NOW - 60 * DAY, lastUsedEpoch = NOW - 5 * 60, lastTestEpoch = NOW - 2 * HOUR, lastTestOk = true,
                    requestsToday = 1240, failuresToday = 2),
                ProviderApiKey("k-1b", "Staging · stage-a", "sk-••••••••••••7d11", priority = 70,
                    addedAtEpoch = NOW - 40 * DAY, lastUsedEpoch = NOW - 30 * 60, lastTestEpoch = NOW - 6 * HOUR, lastTestOk = true,
                    requestsToday = 380, failuresToday = 0),
                ProviderApiKey("k-1c", "Backup · cold", "sk-••••••••••••0c4e", priority = 30, enabled = false,
                    addedAtEpoch = NOW - 200 * DAY, lastUsedEpoch = NOW - 14 * DAY, lastTestEpoch = NOW - 7 * DAY, lastTestOk = false,
                    requestsToday = 0, failuresToday = 0,
                    notes = "Held in reserve — rotate into active if primary fails"),
            ),
            activeKeyId = "k-1a",
            models = listOf(
                AIModel("gpt-4o", "GPT-4o", 128_000, ModelCapability.AGENT, 0.005, true),
                AIModel("gpt-4o-mini", "GPT-4o Mini", 128_000, ModelCapability.CHAT, 0.00015, true),
                AIModel("o1-preview", "o1 Preview", 200_000, ModelCapability.REASONING, 0.015, true),
            )
        ),
        AIProvider(
            id = "p-2", name = "Anthropic-Compatible", region = "US-WEST-2", type = ProviderType.ANTHROPIC_COMPATIBLE,
            baseUrl = "https://api.anthropic.com", priority = 90,
            status = ProviderStatus.ONLINE, latencyMs = 410,
            supportsVision = true, supportsTools = true, monthlyUsagePct = 67,
            apiKeys = listOf(
                ProviderApiKey("k-2a", "Claude Team · primary", "sk-ant-••••••••••••9c81", priority = 100,
                    addedAtEpoch = NOW - 120 * DAY, lastUsedEpoch = NOW - 1 * 60, lastTestEpoch = NOW - 30 * 60, lastTestOk = true,
                    requestsToday = 2104, failuresToday = 4),
                ProviderApiKey("k-2b", "Claude Team · fallback", "sk-ant-••••••••••••4a22", priority = 60,
                    addedAtEpoch = NOW - 90 * DAY, lastUsedEpoch = NOW - 12 * HOUR, lastTestEpoch = NOW - 4 * HOUR, lastTestOk = true,
                    requestsToday = 142, failuresToday = 0),
            ),
            activeKeyId = "k-2a",
            models = listOf(
                AIModel("claude-3.5-sonnet", "Claude 3.5 Sonnet", 200_000, ModelCapability.CODE, 0.003, true),
                AIModel("claude-3.5-haiku", "Claude 3.5 Haiku", 200_000, ModelCapability.CHAT, 0.0008, true),
                AIModel("claude-3-opus", "Claude 3 Opus", 200_000, ModelCapability.REASONING, 0.015, false),
            )
        ),
        AIProvider(
            id = "p-3", name = "Gemini-Compatible", region = "Global", type = ProviderType.GOOGLE_COMPATIBLE,
            baseUrl = "https://generativelanguage.googleapis.com", priority = 70,
            status = ProviderStatus.DEGRADED, latencyMs = 720,
            supportsVision = true, supportsTools = true, monthlyUsagePct = 23,
            apiKeys = listOf(
                ProviderApiKey("k-3a", "Workspace · shared", "AIza••••••••••••0411", priority = 80,
                    addedAtEpoch = NOW - 45 * DAY, lastUsedEpoch = NOW - 8 * HOUR, lastTestEpoch = NOW - 4 * HOUR, lastTestOk = true,
                    requestsToday = 88, failuresToday = 6,
                    cooldownUntilEpoch = NOW - 30 * 60),
            ),
            activeKeyId = "k-3a",
            models = listOf(
                AIModel("gemini-2.0-pro", "Gemini 2.0 Pro", 1_000_000, ModelCapability.LONG_CONTEXT, 0.00125, true),
                AIModel("gemini-2.0-flash", "Gemini 2.0 Flash", 1_000_000, ModelCapability.CHAT, 0.000075, true),
            )
        ),
        AIProvider(
            id = "p-4", name = "Local Ollama Mesh", region = "On-Prem", type = ProviderType.LOCAL_OLLAMA,
            baseUrl = "http://localhost:11434", priority = 50,
            status = ProviderStatus.ONLINE, latencyMs = 85,
            supportsVision = false, supportsTools = false, monthlyUsagePct = 8,
            apiKeys = emptyList(),
            activeKeyId = null,
            models = listOf(
                AIModel("llama3.3-70b", "Llama 3.3 70B (Q4)", 128_000, ModelCapability.CHAT, 0.0, true),
                AIModel("deepseek-coder-v2", "DeepSeek Coder V2", 64_000, ModelCapability.CODE, 0.0, true),
                AIModel("nomic-embed", "Nomic Embed", 8_000, ModelCapability.EMBEDDING, 0.0, true),
            )
        ),
        AIProvider(
            id = "p-5", name = "Custom Sovereign", region = "Internal", type = ProviderType.CUSTOM_HTTP,
            baseUrl = "https://sovereign.musgo.internal/v1", priority = 110,
            status = ProviderStatus.MAINTENANCE, latencyMs = 0,
            supportsVision = true, supportsTools = true, monthlyUsagePct = 91,
            apiKeys = listOf(
                ProviderApiKey("k-5a", "Sovereign · prod", "sov-••••••••••••ee01", priority = 100,
                    addedAtEpoch = NOW - 30 * DAY, lastUsedEpoch = NOW - 1 * DAY, lastTestEpoch = NOW - 12 * HOUR, lastTestOk = false,
                    requestsToday = 0, failuresToday = 0,
                    notes = "Maintenance window — primary is down"),
                ProviderApiKey("k-5b", "Sovereign · shadow", "sov-••••••••••••4b7c", priority = 50, enabled = false,
                    addedAtEpoch = NOW - 25 * DAY, lastTestEpoch = NOW - 7 * DAY, lastTestOk = false,
                    requestsToday = 0, failuresToday = 0,
                    notes = "Shadow traffic only"),
            ),
            activeKeyId = null,
            models = listOf(
                AIModel("musgo-r1", "MusGo-R1", 256_000, ModelCapability.AGENT, 0.0, true),
                AIModel("musgo-coder", "MusGo Coder", 128_000, ModelCapability.CODE, 0.0, true),
            )
        ),
    )

    val projects = listOf(
        Project(
            id = "pr-1", name = "Sovereign Ledger", slug = "sovereign-ledger",
            description = "Immutable audit ledger for civic decision records.",
            status = ProjectStatus.BUILDING, repoFullName = "musgo-foundation/sovereign-ledger",
            branch = "feat/rbac-v2", visibility = ProjectVisibility.PRIVATE,
            tags = listOf("Kotlin", "Postgres", "RBAC"),
            createdAtEpoch = NOW - 30 * DAY, lastBuildAtEpoch = NOW - 2 * HOUR,
            lastDeployAtEpoch = NOW - 3 * DAY, agentCount = 5, skillCount = 12, blueprintCount = 4,
            workflowStepCount = 9,
        ),
        Project(
            id = "pr-2", name = "MusGo Connectors", slug = "musgo-connectors",
            description = "Connector framework — GitHub, GitLab, Bitbucket, local FS.",
            status = ProjectStatus.AWAITING_APPROVAL, repoFullName = "musgo-foundation/connectors",
            branch = "main", visibility = ProjectVisibility.INTERNAL,
            tags = listOf("Kotlin", "Connectors"),
            createdAtEpoch = NOW - 90 * DAY, lastBuildAtEpoch = NOW - 6 * HOUR,
            lastDeployAtEpoch = NOW - 12 * HOUR, agentCount = 5, skillCount = 8, blueprintCount = 6,
            workflowStepCount = 12,
        ),
        Project(
            id = "pr-3", name = "Civic Chat Surface", slug = "civic-chat",
            description = "Mobile-first chat surface for MusGo-OS orchestration.",
            status = ProjectStatus.DEPLOYED, repoFullName = "musgo-foundation/civic-chat",
            branch = "release/1.4", visibility = ProjectVisibility.PUBLIC,
            tags = listOf("Compose", "Android", "Kotlin"),
            createdAtEpoch = NOW - 180 * DAY, lastBuildAtEpoch = NOW - 1 * DAY,
            lastDeployAtEpoch = NOW - 1 * DAY, agentCount = 5, skillCount = 22, blueprintCount = 7,
            workflowStepCount = 14,
        ),
        Project(
            id = "pr-4", name = "Sandbox Runtime", slug = "sandbox-rt",
            description = "Hermetic, isolated build & test sandbox.",
            status = ProjectStatus.ACTIVE, repoFullName = "musgo-foundation/sandbox-rt",
            branch = "main", visibility = ProjectVisibility.PRIVATE,
            tags = listOf("Containers", "Linux", "Cgroups"),
            createdAtEpoch = NOW - 60 * DAY, lastBuildAtEpoch = NOW - 18 * HOUR,
            lastDeployAtEpoch = NOW - 2 * DAY, agentCount = 5, skillCount = 5, blueprintCount = 3,
            workflowStepCount = 8,
        ),
        Project(
            id = "pr-5", name = "Memory Mesh", slug = "memory-mesh",
            description = "Multi-scope memory system with retention policies.",
            status = ProjectStatus.PAUSED, repoFullName = "musgo-foundation/memory-mesh",
            branch = "main", visibility = ProjectVisibility.INTERNAL,
            tags = listOf("Vector", "Postgres"),
            createdAtEpoch = NOW - 45 * DAY, lastBuildAtEpoch = NOW - 5 * DAY,
            lastDeployAtEpoch = null, agentCount = 5, skillCount = 4, blueprintCount = 2,
            workflowStepCount = 6,
        ),
        Project(
            id = "pr-6", name = "Blueprint Index", slug = "blueprint-index",
            description = "Reference index for blueprints & skill chunks.",
            status = ProjectStatus.DRAFT, repoFullName = null,
            branch = "main", visibility = ProjectVisibility.PRIVATE,
            tags = listOf("Index", "Search"),
            createdAtEpoch = NOW - 4 * DAY, lastBuildAtEpoch = null,
            lastDeployAtEpoch = null, agentCount = 5, skillCount = 3, blueprintCount = 1,
            workflowStepCount = 3,
        ),
    )

    val agents = listOf(
        Agent("a-1", "Helios", AgentRole.PLANNER, "Decomposes goals into dependency graphs.", AgentState.IDLE, null, "o1-preview",
            listOf("decompose", "graph", "context-prep"), listOf("sk-planner-1"), listOf("bp-plan"), 0.96, 482, 1240),
        Agent("a-2", "Vulcan", AgentRole.DEVELOPER, "Edits source across repos with diff awareness.", AgentState.RUNNING, "t-101", "claude-3.5-sonnet",
            listOf("edit", "diff", "refactor"), listOf("sk-coder-1", "sk-coder-2"), listOf("bp-spring", "bp-android"), 0.93, 612, 2800),
        Agent("a-3", "Veritas", AgentRole.TESTER, "Generates and executes tests, surfaces regressions.", AgentState.IDLE, null, "gpt-4o",
            listOf("test-gen", "coverage", "regression"), listOf("sk-tester-1"), listOf("bp-junit", "bp-kotest"), 0.91, 388, 1980),
        Agent("a-4", "Forge", AgentRole.BUILDER, "Builds artifacts inside the sandbox.", AgentState.RUNNING, "t-103", "gpt-4o-mini",
            listOf("build", "cache", "parallel"), listOf("sk-build-1"), listOf("bp-gradle", "bp-docker"), 0.97, 521, 3120),
        Agent("a-5", "Atlas", AgentRole.DEPLOYER, "Promotes artifacts to environments with health checks.", AgentState.WAITING_APPROVAL, "t-104", "claude-3.5-haiku",
            listOf("deploy", "rollback", "health"), listOf("sk-deploy-1"), listOf("bp-k8s", "bp-edge"), 0.94, 264, 4100),
        Agent("a-6", "Argus", AgentRole.SECURITY, "Scans PRs and flags redaction failures.", AgentState.IDLE, null, "claude-3.5-sonnet",
            listOf("scan", "redact", "secrets"), listOf("sk-sec-1"), listOf("bp-sec"), 0.98, 211, 1610),
        Agent("a-7", "Clio", AgentRole.DOCS, "Generates docs and changelogs.", AgentState.OFFLINE, null, "gpt-4o-mini",
            listOf("docs", "changelog"), listOf("sk-docs-1"), listOf("bp-md"), 0.92, 142, 980),
        Agent("a-8", "Themis", AgentRole.REVIEWER, "Final human-style review before approval.", AgentState.AWAITING_HUMAN, "t-105", "o1-preview",
            listOf("review", "approve", "block"), listOf("sk-review-1"), listOf("bp-style"), 0.95, 178, 2210),
    )

    val tasks = listOf(
        AgentTask("t-101", "Implement RBAC middleware", "pr-1", "a-2", TaskState.RUNNING, 64, NOW - 30 * 60, null,
            listOf("t-100"), listOf("src/auth/Rbac.kt"), 0, null),
        AgentTask("t-102", "Generate role test cases", "pr-1", "a-3", TaskState.QUEUED, 0, NOW - 25 * 60, null,
            listOf("t-101"), emptyList(), 0, null),
        AgentTask("t-103", "Build debug APK", "pr-1", "a-4", TaskState.RUNNING, 82, NOW - 12 * 60, null,
            listOf("t-102"), listOf("app-debug.apk"), 0, null),
        AgentTask("t-104", "Deploy to staging", "pr-2", "a-5", TaskState.WAITING, 0, NOW - 6 * 60, null,
            listOf("t-103"), emptyList(), 0, "Approval from Dhani"),
        AgentTask("t-105", "Final review of #412", "pr-2", "a-8", TaskState.REVIEW, 50, NOW - 90 * 60, null,
            listOf("t-104"), emptyList(), 0, null),
    )

    val blueprints = listOf(
        Blueprint("bp-spring", "Spring Boot Service", "Spring", "Backend",
            "Production-grade Spring Boot layout with observability.", indexedChunks = 312, trusted = true, stars = 1240, uses = 4821,
            tags = listOf("Java", "Spring", "REST")),
        Blueprint("bp-android", "Android Compose App", "Compose", "Mobile",
            "Modern Material 3 Android app skeleton with Hilt + Navigation.", indexedChunks = 268, trusted = true, stars = 980, uses = 3120,
            tags = listOf("Kotlin", "Compose", "Material")),
        Blueprint("bp-junit", "JUnit 5 Test Suite", "JUnit", "Testing",
            "Parameterized test patterns with coverage reporting.", indexedChunks = 121, trusted = true, stars = 540, uses = 1820,
            tags = listOf("Testing", "JUnit")),
        Blueprint("bp-kotest", "Kotest Property Specs", "Kotest", "Testing",
            "Property-based testing patterns for Kotlin.", indexedChunks = 84, trusted = true, stars = 320, uses = 940,
            tags = listOf("Kotlin", "Property")),
        Blueprint("bp-gradle", "Gradle Multi-Module", "Gradle", "Build",
            "Multi-module Gradle setup with version catalogs.", indexedChunks = 96, trusted = true, stars = 760, uses = 2510,
            tags = listOf("Build", "Gradle")),
        Blueprint("bp-docker", "Distroless Container", "Docker", "Infra",
            "Hardened distroless container template.", indexedChunks = 54, trusted = true, stars = 430, uses = 1280,
            tags = listOf("Docker", "Security")),
        Blueprint("bp-k8s", "Kubernetes Manifest", "Kubernetes", "Infra",
            "Production K8s manifest with probes and PDB.", indexedChunks = 72, trusted = true, stars = 380, uses = 1100,
            tags = listOf("K8s", "Infra")),
        Blueprint("bp-edge", "Edge Function", "WASM", "Edge",
            "WASM edge function template with cold-start optimization.", indexedChunks = 48, trusted = true, stars = 220, uses = 640,
            tags = listOf("Edge", "WASM")),
        Blueprint("bp-md", "Markdown Doc System", "Markdown", "Docs",
            "MkDocs-style doc generator with TOC.", indexedChunks = 32, trusted = false, stars = 90, uses = 410,
            tags = listOf("Docs")),
        Blueprint("bp-sec", "Secrets Handling", "Kotlin", "Security",
            "Reusable patterns for secrets + redaction.", indexedChunks = 64, trusted = true, stars = 540, uses = 1640,
            tags = listOf("Security", "Vault")),
        Blueprint("bp-style", "Review Checklist", "Markdown", "Review",
            "Human-style review checklist with severity matrix.", indexedChunks = 28, trusted = true, stars = 160, uses = 510,
            tags = listOf("Review")),
        Blueprint("bp-plan", "Task Decomposer", "Kotlin", "Planning",
            "Decomposes goals into typed step graphs.", indexedChunks = 88, trusted = true, stars = 410, uses = 1210,
            tags = listOf("Planning", "Graph")),
    )

    val skills = listOf(
        Skill("sk-planner-1", "goal-decomposer", "1.4.0", SkillSource.GITHUB, "Planning",
            "Decompose a goal into typed steps with dependencies.", true, true, 48, listOf("plan", "decompose"), 312),
        Skill("sk-coder-1", "kotlin-refactor", "2.1.0", SkillSource.AGENT_CREATED, "Coding",
            "Refactor Kotlin classes while preserving public API.", true, true, 96, listOf("kotlin", "refactor"), 218),
        Skill("sk-coder-2", "diff-aware-edit", "1.7.2", SkillSource.UPLOAD, "Coding",
            "Patch files using unified diff awareness.", true, true, 72, listOf("diff", "patch"), 411),
        Skill("sk-tester-1", "junit5-gen", "3.0.1", SkillSource.PASTED, "Testing",
            "Generate JUnit 5 tests for a class.", true, true, 64, listOf("test", "junit"), 188),
        Skill("sk-build-1", "gradle-multi-build", "1.2.5", SkillSource.GITHUB, "Build",
            "Build multi-module Gradle projects with caching.", true, true, 88, listOf("gradle", "build"), 92),
        Skill("sk-deploy-1", "k8s-promote", "2.3.0", SkillSource.URL, "Deploy",
            "Promote artifacts to K8s clusters with health gates.", true, true, 110, listOf("k8s", "deploy"), 64),
        Skill("sk-sec-1", "secret-redactor", "1.5.0", SkillSource.AGENT_CREATED, "Security",
            "Redact secrets in logs and source.", true, true, 32, listOf("redact", "secret"), 290),
        Skill("sk-docs-1", "changelog-gen", "1.1.0", SkillSource.ZIP, "Docs",
            "Generate changelogs from commit history.", true, false, 28, listOf("docs", "changelog"), 41),
        Skill("sk-review-1", "human-style-review", "0.9.4", SkillSource.UPLOAD, "Review",
            "Apply human-style review heuristics to diffs.", true, true, 56, listOf("review"), 73),
        Skill("sk-sandbox-1", "hermetic-runner", "1.0.0", SkillSource.GITHUB, "Sandbox",
            "Run commands hermetically with no network.", true, true, 64, listOf("sandbox"), 56),
    )

    val pats = listOf(
        GitHubPat("pat-1", "Dhani (Personal)", "ghp_••••••••••••3f2a", listOf("repo", "read:org"), NOW - 60 * DAY, NOW - 1 * HOUR, PatStatus.ACTIVE),
        GitHubPat("pat-2", "Org Bot", "ghp_••••••••••••9c81", listOf("repo", "workflow"), NOW - 200 * DAY, NOW - 2 * DAY, PatStatus.EXPIRING),
        GitHubPat("pat-3", "Legacy CI", "ghp_••••••••••••0411", listOf("repo"), NOW - 380 * DAY, NOW - 30 * DAY, PatStatus.REVOKED),
    )

    val repos = listOf(
        GitHubRepo("r-1", "musgo-foundation/sovereign-ledger", "feat/rbac-v2", ProjectVisibility.PRIVATE, "main", 4, 12,
            "8f1c2a4", "feat(rbac): add role inheritance", 12_400, "pr-1"),
        GitHubRepo("r-2", "musgo-foundation/connectors", "main", ProjectVisibility.INTERNAL, "main", 2, 8,
            "1a9b3f0", "fix(github): handle 422 on PR create", 6_240, "pr-2"),
        GitHubRepo("r-3", "musgo-foundation/civic-chat", "release/1.4", ProjectVisibility.PUBLIC, "main", 3, 22,
            "c8e4a01", "chore(release): 1.4.0", 28_900, "pr-3"),
        GitHubRepo("r-4", "musgo-foundation/sandbox-rt", "main", ProjectVisibility.PRIVATE, "main", 1, 4,
            "2d3e9aa", "perf(sandbox): reuse worktrees", 4_120, "pr-4"),
        GitHubRepo("r-5", "musgo-foundation/memory-mesh", "main", ProjectVisibility.INTERNAL, "main", 0, 2,
            "9f0a1c2", "docs: retention policies", 2_080, "pr-5"),
    )

    val pullRequests = listOf(
        PullRequest("prq-1", 412, "musgo-foundation/connectors", "feat(connector): bitbucket adapter", PrState.OPEN,
            "vulcan-agent", "feat/bitbucket", "main", true, 612, 84, NOW - 6 * HOUR, "pr-2"),
        PullRequest("prq-2", 87, "musgo-foundation/sovereign-ledger", "feat(rbac): role inheritance", PrState.OPEN,
            "vulcan-agent", "feat/rbac-v2", "main", true, 482, 12, NOW - 2 * HOUR, "pr-1"),
        PullRequest("prq-3", 144, "musgo-foundation/civic-chat", "release: 1.4.0", PrState.MERGED,
            "atlas-agent", "release/1.4", "main", true, 1840, 920, NOW - 1 * DAY, "pr-3"),
        PullRequest("prq-4", 22, "musgo-foundation/sandbox-rt", "perf: reuse worktrees", PrState.DRAFT,
            "forge-agent", "perf/worktree", "main", false, 220, 48, NOW - 18 * HOUR, "pr-4"),
        PullRequest("prq-5", 6, "musgo-foundation/memory-mesh", "docs: retention policies", PrState.OPEN,
            "clio-agent", "docs/retention", "main", true, 48, 8, NOW - 5 * DAY, "pr-5"),
    )

    val sandboxes = listOf(
        Sandbox("sb-1", "pr-1", SandboxState.EXECUTING, "/tmp/sandboxes/pr-1-a41f", NOW - 2 * HOUR, NOW + 22 * HOUR,
            networkAccess = false, resourceLimitMb = 8192, cpuLimit = "4 cores", commandCount = 38, logCount = 412,
            clonedSha = "8f1c2a4", runtime = SandboxRuntime.LOCAL_SHELL),
        Sandbox("sb-2", "pr-2", SandboxState.AWAITING_APPROVAL, "/tmp/sandboxes/pr-2-b821", NOW - 6 * HOUR, NOW + 18 * HOUR,
            networkAccess = true, resourceLimitMb = 4096, cpuLimit = "2 cores", commandCount = 22, logCount = 198,
            clonedSha = "1a9b3f0", runtime = SandboxRuntime.REMOTE_DOCKER,
            remoteEndpoint = "ssh://forge@build-cluster.musgo.internal:2222"),
        Sandbox("sb-3", "pr-3", SandboxState.DESTROYED, "/tmp/sandboxes/pr-3-c1a2", NOW - 1 * DAY, NOW - 1 * DAY + 24 * HOUR,
            networkAccess = false, resourceLimitMb = 8192, cpuLimit = "4 cores", commandCount = 64, logCount = 720,
            clonedSha = "c8e4a01", runtime = SandboxRuntime.LOCAL_SHELL),
        Sandbox("sb-4", "pr-4", SandboxState.READY, "/tmp/sandboxes/pr-4-d8e1", NOW - 18 * HOUR, NOW + 6 * HOUR,
            networkAccess = false, resourceLimitMb = 4096, cpuLimit = "2 cores", commandCount = 12, logCount = 84,
            clonedSha = "2d3e9aa", runtime = SandboxRuntime.LOCAL_SHELL),
    )

    val terminals = listOf(
        TerminalSession("t-1", "sb-1", TerminalKind.LOCAL_SHELL, TerminalState.CONNECTED,
            startedAtEpoch = NOW - 2 * HOUR, lastCommandAtEpoch = NOW - 60, commandCount = 38,
            endpoint = "shell://sandbox/sb-1", requiresRemote = false),
        TerminalSession("t-2", "sb-2", TerminalKind.SSH, TerminalState.CONNECTED,
            startedAtEpoch = NOW - 6 * HOUR, lastCommandAtEpoch = NOW - 12 * 60, commandCount = 22,
            endpoint = "ssh://forge@build-cluster.musgo.internal:2222", requiresRemote = true),
        TerminalSession("t-3", "sb-1", TerminalKind.MCP, TerminalState.IDLE,
            startedAtEpoch = NOW - 1 * HOUR, lastCommandAtEpoch = NOW - 8 * 60, commandCount = 6,
            endpoint = "mcp://musgo-local/tools", requiresRemote = false),
    )

    val sandboxLogs = listOf(
        SandboxLog("l-1", "sb-1", LogStream.SYSTEM, "init", "Sandbox provisioned: cgroup v2, no-net", NOW - 2 * HOUR, 0),
        SandboxLog("l-2", "sb-1", LogStream.STDOUT, "git clone", "Cloning into '/tmp/sandboxes/pr-1-a41f'...", NOW - 2 * HOUR + 60, 0),
        SandboxLog("l-3", "sb-1", LogStream.STDOUT, "gradle build", "BUILD SUCCESSFUL in 42s", NOW - 90 * 60, 0),
        SandboxLog("l-4", "sb-2", LogStream.APPROVAL, "deploy --to staging", "Awaiting human approval", NOW - 30 * 60, null),
        SandboxLog("l-5", "sb-1", LogStream.STDERR, "ktlint", "warning: unused import (Auth.kt:12)", NOW - 60 * 60, 0),
    )

    val workflows = listOf(
        Workflow("wf-1", "pr-1", "Sovereign Ledger — RBAC v2", WorkflowState.RUNNING, 4, NOW - 3 * HOUR, NOW - 5 * 60,
            steps = listOf(
                WorkflowStep("s-1", 0, "Plan RBAC scope", StepKind.PLAN, StepState.DONE, "a-1", emptyList(), 600, NOW - 3 * HOUR, NOW - 3 * HOUR + 600),
                WorkflowStep("s-2", 1, "Implement role middleware", StepKind.CODE, StepState.DONE, "a-2", listOf("s-1"), 1800, NOW - 3 * HOUR + 600, NOW - 3 * HOUR + 1800),
                WorkflowStep("s-3", 2, "Generate tests", StepKind.TEST, StepState.RUNNING, "a-3", listOf("s-2"), 1200, NOW - 90 * 60, null),
                WorkflowStep("s-4", 3, "Build APK", StepKind.BUILD, StepState.PENDING, "a-4", listOf("s-3"), 2400, null, null, parallel = true),
                WorkflowStep("s-5", 4, "Commit & push", StepKind.PUSH, StepState.PENDING, "a-2", listOf("s-3"), 600, null, null),
                WorkflowStep("s-6", 5, "Open PR", StepKind.PR, StepState.PENDING, "a-2", listOf("s-5"), 300, null, null),
                WorkflowStep("s-7", 6, "Review", StepKind.REVIEW, StepState.PENDING, "a-8", listOf("s-6"), 900, null, null),
                WorkflowStep("s-8", 7, "Approval gate", StepKind.APPROVAL, StepState.PENDING, null, listOf("s-7"), 86400, null, null),
                WorkflowStep("s-9", 8, "Deploy staging", StepKind.DEPLOY, StepState.PENDING, "a-5", listOf("s-8"), 1200, null, null),
            ),
            requiresHumanApproval = true,
        ),
        Workflow("wf-2", "pr-2", "Connectors — Bitbucket adapter", WorkflowState.AWAITING_APPROVAL, 6, NOW - 6 * HOUR, NOW - 30 * 60,
            steps = listOf(
                WorkflowStep("s2-1", 0, "Plan adapter", StepKind.PLAN, StepState.DONE, "a-1", emptyList(), 600, NOW - 6 * HOUR, NOW - 6 * HOUR + 600),
                WorkflowStep("s2-2", 1, "Implement client", StepKind.CODE, StepState.DONE, "a-2", listOf("s2-1"), 2400, NOW - 6 * HOUR + 600, NOW - 4 * HOUR),
                WorkflowStep("s2-3", 2, "Integration tests", StepKind.TEST, StepState.DONE, "a-3", listOf("s2-2"), 1800, NOW - 4 * HOUR, NOW - 3 * HOUR),
                WorkflowStep("s2-4", 3, "Build artifact", StepKind.BUILD, StepState.DONE, "a-4", listOf("s2-3"), 1200, NOW - 3 * HOUR, NOW - 3 * HOUR + 1200),
                WorkflowStep("s2-5", 4, "Commit & push", StepKind.PUSH, StepState.DONE, "a-2", listOf("s2-4"), 300, NOW - 3 * HOUR + 1200, NOW - 3 * HOUR + 1500),
                WorkflowStep("s2-6", 5, "Open PR #412", StepKind.PR, StepState.DONE, "a-2", listOf("s2-5"), 300, NOW - 3 * HOUR + 1500, NOW - 3 * HOUR + 1800),
                WorkflowStep("s2-7", 6, "Awaiting human approval", StepKind.APPROVAL, StepState.WAITING_APPROVAL, null, listOf("s2-6"), 86400, null, null),
            ),
            requiresHumanApproval = true,
        ),
    )

    val memoryEntries = listOf(
        MemoryEntry("m-1", MemoryScope.USER, "u-001", "Always redact secrets in PR descriptions",
            "User explicitly requested: never include raw API keys or tokens in any PR body, commit message, or comment.",
            listOf("preference", "security"), NOW - 90 * DAY, 365, true),
        MemoryEntry("m-2", MemoryScope.USER, "u-001", "Prefer Compose over Views",
            "All new Android UI must use Jetpack Compose + Material 3.",
            listOf("android", "preference"), NOW - 60 * DAY, 365, true),
        MemoryEntry("m-3", MemoryScope.PROJECT, "pr-1", "Ledger uses Kotlin + Postgres + Flyway",
            "Tech stack must remain: Kotlin, Postgres, Flyway. Do not introduce JPA.",
            listOf("stack", "constraint"), NOW - 30 * DAY, 180, true),
        MemoryEntry("m-4", MemoryScope.PROJECT, "pr-2", "Connector framework is protocol-agnostic",
            "All connectors must implement the Connector interface and never leak vendor types.",
            listOf("architecture"), NOW - 45 * DAY, 180, false),
        MemoryEntry("m-5", MemoryScope.TASK, "t-101", "RBAC implementation in progress",
            "Vulcan is editing src/auth/Rbac.kt; do not start parallel edits to the same file.",
            listOf("active"), NOW - 30 * 60, 7, false),
        MemoryEntry("m-6", MemoryScope.AGENT, "a-2", "Vulcan preferred model: Claude Sonnet",
            "Use claude-3.5-sonnet for code edits; fall back to gpt-4o-mini.",
            listOf("preference"), NOW - 14 * DAY, 180, true),
        MemoryEntry("m-7", MemoryScope.WORKFLOW, "wf-1", "RBAC v2 requires human approval",
            "Production deploy of RBAC changes must include a manual approval step.",
            listOf("approval", "policy"), NOW - 7 * DAY, 90, true),
        MemoryEntry("m-8", MemoryScope.HANDOVER, "pr-1", "Handover: Vulcan → Veritas",
            "RBAC middleware done. Veritas: continue with tests, target 85% coverage.",
            listOf("handover"), NOW - 60 * 60, 14, false),
    )

    val handovers = listOf(
        Handover(
            "h-1", "a-2", "a-3", "pr-1", "Generate RBAC test suite",
            currentState = "Middleware implemented, src/auth/Rbac.kt compiles.",
            completedWork = listOf("Added Role hierarchy", "Implemented guards", "Added context propagation"),
            remainingWork = listOf("Generate happy-path tests", "Generate denial tests", "Add coverage report"),
            nextAction = "Use sk-tester-1 to produce JUnit5 spec for src/auth/Rbac.kt",
            filesInvolved = listOf("src/auth/Rbac.kt", "src/auth/Roles.kt", "src/auth/Guards.kt"),
            dependencies = listOf("junit5", "mockk", "kotest-property"),
            blockers = emptyList(),
            decisions = listOf("Use role inheritance", "No JPA"),
            skillsActive = listOf("sk-tester-1"),
            blueprintsActive = listOf("bp-junit"),
            repoSha = "8f1c2a4",
            buildArtifactId = null,
            createdAtEpoch = NOW - 60 * 60,
            resumedByAgentId = "a-3",
        ),
        Handover(
            "h-2", "a-4", "a-5", "pr-2", "Promote connector build to staging",
            currentState = "Artifact verified, PR #412 open, checks passing.",
            completedWork = listOf("Built", "Pushed", "Opened PR"),
            remainingWork = listOf("Wait for approval", "Deploy staging", "Health check"),
            nextAction = "Block on approval; then run deploy --to staging",
            filesInvolved = listOf("connectors-bitbucket/build/libs/connectors-bitbucket.jar"),
            dependencies = listOf("k8s-staging"),
            blockers = listOf("Human approval"),
            decisions = listOf("Staging first, canary second"),
            skillsActive = listOf("sk-deploy-1"),
            blueprintsActive = listOf("bp-k8s"),
            repoSha = "1a9b3f0",
            buildArtifactId = "art-2",
            createdAtEpoch = NOW - 30 * 60,
        ),
    )

    val artifacts = listOf(
        BuildArtifact("art-1", "pr-1", "1.4.0-rc3", "8f1c2a4", ArtifactType.APK, 18.4, "sha256:9f1a...e2",
            NOW - 90 * 60, null, ArtifactStatus.VERIFIED),
        BuildArtifact("art-2", "pr-2", "0.9.1", "1a9b3f0", ArtifactType.JAR, 4.2, "sha256:2c8b...1d",
            NOW - 3 * HOUR, "staging", ArtifactStatus.PENDING),
        BuildArtifact("art-3", "pr-3", "1.4.0", "c8e4a01", ArtifactType.AAB, 26.8, "sha256:a019...c7",
            NOW - 1 * DAY, "production", ArtifactStatus.DEPLOYED),
        BuildArtifact("art-4", "pr-4", "0.3.0", "2d3e9aa", ArtifactType.IMAGE, 312.0, "sha256:44a2...b9",
            NOW - 18 * HOUR, null, ArtifactStatus.VERIFIED),
        BuildArtifact("art-5", "pr-1", "1.3.7", "c0a911d", ArtifactType.APK, 17.9, "sha256:7711...0a",
            NOW - 7 * DAY, "production", ArtifactStatus.SUPERSEDED),
    )

    val deployments = listOf(
        Deployment("d-1", "pr-2", "art-2", DeployEnvironment.STAGING, DeployState.QUEUED, "0.9.1", "https://staging.musgo.dev",
            NOW - 30 * 60, null, false, true, "Awaiting approval; will run health gate."),
        Deployment("d-2", "pr-3", "art-3", DeployEnvironment.PRODUCTION, DeployState.LIVE, "1.4.0", "https://musgo.dev",
            NOW - 1 * DAY, NOW - 1 * DAY + 600, true, true, "All checks green; canary 100%."),
        Deployment("d-3", "pr-3", "art-3", DeployEnvironment.CANARY, DeployState.LIVE, "1.4.0", "https://canary.musgo.dev",
            NOW - 1 * DAY, NOW - 1 * DAY + 300, true, true, "10% traffic; p95 < 180ms."),
        Deployment("d-4", "pr-4", "art-4", DeployEnvironment.EDGE, DeployState.LIVE, "0.3.0", "https://edge.musgo.dev",
            NOW - 18 * HOUR, NOW - 18 * HOUR + 240, true, false, "Edge replicas rolled out."),
        Deployment("d-5", "pr-1", "art-5", DeployEnvironment.PRODUCTION, DeployState.ROLLED_BACK, "1.3.7", null,
            NOW - 7 * DAY, NOW - 7 * DAY + 200, false, true, "Rolled back after auth regression."),
    )

    val auditEvents = listOf(
        AuditEvent("au-1", NOW - 5 * 60, "vulcan-agent", "code.edit", "src/auth/Rbac.kt", AuditSeverity.NOTICE, "ok"),
        AuditEvent("au-2", NOW - 30 * 60, "atlas-agent", "deploy.request", "staging", AuditSeverity.WARNING, "awaiting-approval"),
        AuditEvent("au-3", NOW - 2 * HOUR, "argus-agent", "secret.scan", "pr-1", AuditSeverity.INFO, "clean"),
        AuditEvent("au-4", NOW - 6 * HOUR, "vulcan-agent", "code.edit", "src/connectors/Bitbucket.kt", AuditSeverity.NOTICE, "ok"),
        AuditEvent("au-5", NOW - 1 * DAY, "atlas-agent", "deploy.complete", "production", AuditSeverity.INFO, "ok"),
        AuditEvent("au-6", NOW - 7 * DAY, "atlas-agent", "deploy.rollback", "production", AuditSeverity.CRITICAL, "auth-regression"),
    )

    val security = SecurityPosture(
        secretCount = 14,
        patCount = 3,
        oauthCount = 0,
        sandboxEgressAllowed = false,
        dangerousActionConfirm = true,
        redactionEnabled = true,
        lastScanEpoch = NOW - 2 * HOUR,
        threats = listOf("pat-2 expires in 9 days", "pat-3 already revoked"),
    )

    val chatSessions = listOf(
        ChatSession("c-1", "Plan RBAC v2 scope", "pr-1", "p-2", "claude-3.5-sonnet", RoutingMode.AUTO_BALANCED, 24, 18_420, NOW - 30 * 60, true),
        ChatSession("c-2", "Investigate connector 422", "pr-2", "p-1", "gpt-4o", RoutingMode.MANUAL, 12, 9_120, NOW - 2 * HOUR, false),
        ChatSession("c-3", "Compose theming review", "pr-3", "p-4", "llama3.3-70b", RoutingMode.AUTO_FREE, 8, 4_320, NOW - 1 * DAY, false),
        ChatSession("c-4", "Sandbox cgroup tuning", "pr-4", "p-2", "claude-3.5-haiku", RoutingMode.AUTO_QUICK, 16, 7_840, NOW - 3 * DAY, false),
    )

    fun buildSampleMessages(sessionId: String): List<ChatMessage> {
        return when (sessionId) {
            "c-1" -> listOf(
                ChatMessage("cm-1", "c-1", MessageRole.USER, "Plan the RBAC v2 implementation across the ledger project. Use the existing role model.", NOW - 90 * 60, bookmarked = true),
                ChatMessage("cm-2", "c-1", MessageRole.AGENT_PLANNER, "Decomposing goal: 4 workstreams, 9 workflow steps, 3 dependencies on tests.", NOW - 88 * 60, providerUsed = "Anthropic", modelUsed = "claude-3.5-sonnet"),
                ChatMessage("cm-3", "c-1", MessageRole.ASSISTANT, "Proposed plan:\n1. Introduce `Role` hierarchy (Admin > Editor > Viewer)\n2. Implement guard middleware in `src/auth/Rbac.kt`\n3. Generate JUnit5 spec via `sk-tester-1`\n4. Open PR with redaction enforced.", NOW - 85 * 60, tokensIn = 220, tokensOut = 480, bookmarked = true),
                ChatMessage("cm-4", "c-1", MessageRole.USER, "Looks good. Start with step 2 and run tests after.", NOW - 80 * 60),
                ChatMessage("cm-5", "c-1", MessageRole.AGENT_DEVELOPER, "Editing `src/auth/Rbac.kt`… patch applied, 14 lines changed.", NOW - 70 * 60, providerUsed = "Anthropic", modelUsed = "claude-3.5-sonnet"),
                ChatMessage("cm-6", "c-1", MessageRole.AGENT_TESTER, "Generated 18 tests in `RbacTest.kt`. Coverage 87%.", NOW - 60 * 60, providerUsed = "OpenAI", modelUsed = "gpt-4o"),
                ChatMessage("cm-7", "c-1", MessageRole.ASSISTANT, "Step 2 complete. Tests pass. Ready for build.", NOW - 55 * 60),
            )
            "c-2" -> listOf(
                ChatMessage("cm-8", "c-2", MessageRole.USER, "We're getting 422 from the GitHub PR create endpoint when the head branch is missing.", NOW - 3 * HOUR),
                ChatMessage("cm-9", "c-2", MessageRole.ASSISTANT, "Let me check the connector code in `src/connectors/Github.kt`.", NOW - 3 * HOUR + 60),
                ChatMessage("cm-10", "c-2", MessageRole.AGENT_DEVELOPER, "Found it: missing `head` field on push ref creation. Patched.", NOW - 2 * HOUR, providerUsed = "OpenAI", modelUsed = "gpt-4o"),
            )
            else -> emptyList()
        }
    }
}
