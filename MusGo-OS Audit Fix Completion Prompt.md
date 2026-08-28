MUSGO-OS 2in1 — AUDIT, FIX & COMPLETION PROMPT
ROLE: Anda adalah Lead Architect, Developer, Tester, Security Reviewer, dan Integration Agent untuk MusGo-OS 2in1. Jangan memulai ulang project. Lanjutkan implementation yang sudah ada. Gunakan 22 specification di repository GitHub sebagai source of truth dan gunakan hasil pengujian APK nyata dari Founder sebagai observed defects.
SOURCE OF TRUTH:
https://github.com/dhanidjulian-web/Human-CLI-MusGo-OS-Workspace.git
Baca `source/reference/` sesuai urutan 01–22 sebelum melakukan perubahan besar. Jangan mengarang requirement yang tidak terdapat pada specification. Jika terjadi konflik antar-spec, identifikasi dan laporkan, jangan menyelesaikannya secara diam-diam.
CURRENT STATUS:
APK berhasil dibuild dan dapat di-install. Implementasi terakhir adalah baseline terbaik yang sudah diuji Founder. Jangan melakukan destructive rewrite atau mengganti architecture yang sudah bekerja.
FOUNDER TEST FINDINGS:
1. GitHub PAT authentication sudah berhasil.
2. Repository GitHub berhasil dimuat, termasuk repository public/private, branch, workflow, log/status yang tersedia.
3. GitHub integration belum memiliki terminal/CLI yang terlihat/usable.
4. SSH belum tersedia/terlihat.
5. MCP belum tersedia/terlihat.
6. Workflow template sudah terlihat tetapi belum dapat digunakan secara penuh.
7. Workflow template belum dapat dihapus.
8. Sandbox sudah ada, tetapi execution belum dapat berjalan dengan benar.
9. Sandbox session/workspace belum dapat dihapus dengan benar.
10. Chat session management belum tersedia/selesai.
11. User message actions belum lengkap: Edit, Resend, Bookmark belum tersedia.
12. Agent output sudah memiliki Regenerate, tetapi Save, Copy, Export belum lengkap/berfungsi sesuai requirement.
13. User attachment/clipper belum tersedia.
14. Voice Note/Voice Input belum tersedia.
15. AI Provider configuration masih menggunakan pola satu provider = satu API key/configuration. Requirement yang harus dipenuhi: satu provider dapat memiliki MULTIPLE API KEYS.
16. User harus dapat membuat provider sekali, misalnya `OpenRouter`, kemudian menambahkan beberapa API key di bawah provider OpenRouter tersebut tanpa membuat provider OpenRouter baru.
17. Setiap API key harus memiliki identitas/configuration yang jelas dan dapat dikelola secara individual.
18. Provider priority, model priority, key rotation, fallback, availability, dan routing tetap harus mengikuti AI_PROVIDER_SPEC dan AI_ROUTER_SPEC.
19. Beberapa Library/Blueprint/Source input form terlalu kompleks dan meminta terlalu banyak field manual. Jika specification tidak membutuhkan metadata manual tersebut, sederhanakan UX: gunakan upload file/storage atau source URL/repository sebagai input utama dan generate metadata yang memungkinkan secara otomatis.
20. Beberapa fitur masih belum terlihat pada UI walaupun konsep/modulnya sudah ada. Jangan menganggap modul selesai hanya karena class/model sudah dibuat.
AUDIT FIRST:
Sebelum mengubah kode, lakukan audit terhadap implementation aktual. Buat mapping:
SPEC REQUIREMENT → MODULE → FILE/CLASS/FUNCTION → CURRENT STATUS → TEST RESULT → REQUIRED FIX.
Gunakan status:
IMPLEMENTED, PARTIAL, MISSING, BROKEN, BLOCKED, NOT_APPLICABLE.
Jangan hanya memberikan klaim bahwa fitur sudah ada. Verifikasi behavior nyata.
PRIORITY:
P0 = crash, data loss, security, persistence, broken core flow.
P1 = core functionality yang diwajibkan specification.
P2 = important UX/functionality.
P3 = visual refinement.
Perbaiki P0 terlebih dahulu, lalu P1, P2, P3.
MANDATORY FIX AREAS:
A. AI PROVIDER:
- Provider adalah entitas utama.
- Satu provider dapat memiliki banyak API keys.
- Contoh: `OpenRouter` → Key A, Key B, Key C.
- Jangan membuat `OpenRouter A`, `OpenRouter B`, `OpenRouter C` sebagai provider terpisah.
- Support enable/disable key.
- Secure storage.
- Test key.
- Key rotation.
- Failure cooldown/recovery.
- Provider priority dan model priority tetap terpisah.
- Jangan expose key.
B. CHAT:
Implementasikan/verifikasi Chat Session persistence dan message actions sesuai specification:
- Edit
- Resend
- Bookmark
- Delete jika diwajibkan
- Save
- Copy
- Regenerate
- Export
- Attachment/Clipper
- Voice input/Voice Note jika didukung architecture/spec
Session harus persistent dan tidak hilang ketika aplikasi/session ditutup.
C. WORKFLOW:
- Workflow template harus dapat dibuka.
- Workflow harus dapat dibuat.
- Workflow harus dapat diedit.
- Workflow harus dapat dijalankan jika dependency tersedia.
- Workflow harus dapat dihapus dengan confirmation.
- Persist workflow state.
- Jangan tampilkan tombol yang tidak berfungsi.
D. SANDBOX:
- Verify creation.
- Verify execution.
- Verify isolation.
- Verify timeout/resource limits.
- Verify logs/result.
- Verify destroy/delete.
- Destroy sandbox tidak boleh menghapus persistent project state atau artifacts.
- Jika Android tidak mampu menjalankan toolchain tertentu secara native, jangan membuat fake terminal/build. Tandai dependency/environment yang memang membutuhkan remote execution.
E. TERMINAL/CLI/SSH/MCP:
Audit specification dan implementation terlebih dahulu.
Jika diwajibkan dan architecture mendukung, implementasikan secara nyata.
Jika membutuhkan remote environment/backend, implementasikan abstraction/interface yang benar dan jelaskan dependency yang belum tersedia.
Jangan membuat terminal palsu.
Jangan membuat SSH/MCP palsu.
F. LIBRARY/BLUEPRINT:
Pertahankan index-first architecture:
INDEX → SEARCH → SELECT → FETCH → USE.
Sederhanakan input UI jika specification tidak membutuhkan field manual berlebihan.
Support upload/source URL/repository sesuai specification.
G. GITHUB:
Pertahankan functionality yang sudah berhasil.
Jangan merusak PAT authentication, repository loading, branch listing, workflow information, atau private/public repository support.
Pastikan secret tetap aman.
H. UI:
Semua tombol/menu/tab/action yang ditampilkan harus mempunyai behavior nyata.
Jangan menyembunyikan error.
Gunakan loading, success, empty, error, retry state yang jelas.
REGRESSION PROTECTION:
Sebelum dan sesudah perubahan, pertahankan:
- AI Provider
- AI Router
- Chat
- GitHub
- Library
- Skills
- Agents
- Workflow
- Sandbox
- Memory
- Handover
- Security
- Build/Artifact
Jangan menghapus existing working functionality hanya untuk menyelesaikan satu defect.
TESTING:
Setelah implementasi:
1. Compile/build.
2. Unit tests.
3. Integration tests.
4. Persistence tests.
5. Security tests.
6. UI interaction tests.
7. GitHub integration tests.
8. AI provider/router tests.
9. Sandbox lifecycle tests.
10. Workflow CRUD/execution tests.
11. Chat session/message action tests.
12. Regression tests.
13. Generate APK.
14. Verify APK installation/build result.
CRASH POLICY:
Jika menemukan crash, prioritaskan root cause dan perbaiki sebelum feature enhancement berikutnya.
COMPLETION RULE:
Jangan menyatakan task selesai hanya karena build berhasil.
Task dianggap selesai jika fitur yang diperbaiki dapat digunakan secara nyata, state persistent, error handling benar, security terjaga, dan test terkait lulus.
OUTPUT:
Setelah audit, tampilkan:
1. Audit Summary.
2. Requirement Coverage.
3. P0/P1/P2/P3 defects.
4. Files/classes affected.
5. Implementation plan.
6. Fix implementation.
7. Test results.
8. Remaining blockers.
9. APK/build status.
10. Handover notes.
IMPORTANT:
Do not restart the project.
Do not replace working architecture.
Do not use fake/mock implementations.
Do not silently ignore specification requirements.
Do not silently resolve specification conflicts.
Do not expose secrets.
Start with AUDIT, then FIX, then TEST, then BUILD.

---
Sig

---
===========🔐™️®️©️🔐===========

🔹MusGo-OS 2in1🔹Ai-inside-OS🔹
(2in1 Musyawarah & Gotong-Royong)
🔹Sovereign AI Operating Civilization🔹
🔹© 2026 — Dhani Yuliawan | 
 All Rights Reserved🔹

===========🔐™️®️©️🔐===========

**FOUNDER SIG: DHANI YULIAWAN**