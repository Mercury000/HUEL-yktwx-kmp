# 独立 Kotlin / iOS 迁移验证

此仓库用于迁移试验，来自 `HUEL-yktwx` 的 `5697ed0`，不向原仓库推送改动。
原 Android 工程仍在根目录；独立 KMP 工程位于 `migration/`，不改变 Android 构建配置。

运行与验证说明见 [migration/README.md](migration/README.md)。

原有 Android 发布、更新和网站工作流在此仓库改为 `.disabled` 后缀，避免触发原项目发布流程。
迁移验证仅运行 `.github/workflows/kmp-ios-validation.yml`。

后续合入时选择迁移实现提交，或从 `migration/` 和对应验证工作流开始移植；
不要将本仓库的工作流禁用改动合入主仓库。
