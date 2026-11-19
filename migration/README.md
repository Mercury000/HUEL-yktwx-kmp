# Kotlin Multiplatform / iOS 迁移验证

本阶段验证现有 Kotlin 业务规则、付款布局、Miuix 图标、模糊和玻璃弹窗在 iOS 的可移植性。
这是局部技术验证工程，不是完整 iOS 客户端。

验证结果与模拟器截图见 [第一轮验证记录](../docs/migration-validation.md)。

## 共享方式

根目录的 `app/src/` 是本仓库业务逻辑的唯一来源。Gradle 在构建时选择原文件生成 KMP 源码：

- `Models.kt`、`ReadFailure.kt`、`NetworkReadAccess.kt`、`ReadController.kt`；
- 原安全键盘、读取门控和读取生命周期测试转换为 `kotlin.test`，同时运行 JVM / iOS 测试；
- `BarBlur.kt`、`AppIcons.kt`、`PaymentCodeNumber.kt` 原样复用；
- 从 `AppAppearance.kt`、`AppearanceControls.kt` 抽取原玻璃宿主、顶部栏、按钮和弹窗实现；
- 从 `PaymentPage.kt` 抽取原付款卡片测量代码，使用原 OS4 玻璃导航组件和图标。
- 复用原 `CompactButton` 的尺寸、颜色与字体，Android 自定义按压反馈边界使用公共 Indication。

业务源码仅将一处 JVM 时钟替换为 Kotlin 公共时钟，测试仅替换 JUnit 入口。
beta01 的 `BlurTopAppBar` 是内部 API。验证宿主的导航标题透明度固定为 1，因此将其零模糊降级分支映射为公开的 `TopAppBar`；原玻璃顶部栏分支保持原样。
生成文件在 `build/` 内，不作为第二套源码维护。以后更新上游文件，构建会直接使用更新后的逻辑。

付款正常状态的布局来自上游 `PaymentPage.kt`；相机、支付 SDK、Android bitmap 生成和宿主导航是平台边界。
本阶段使用静态虚构码图、OS4 玻璃导航、简化页面路由和本地设置页，保留付款卡片的尺寸、间距与字体。
手机悬浮导航模式下仅保留底部扫码入口，与原 `YktApp.kt` 的显示策略一致。
尚未声称整个 UI、动画、字体缩放和系统手势完全一致。

## 依赖与环境

- Kotlin `2.4.21`、Compose Multiplatform `1.12.2`、JDK 21；
- Miuix 各模块统一使用 Maven Central 的 **`0.9.5-beta01`**，无需 GitHub Packages 密钥；
- 复用根目录 Gradle Wrapper，通过 `-p migration` 使用独立构建；
- Windows 可运行共享逻辑测试和桌面预览；iOS 编译、模拟器测试使用 macOS。

## 本地验证

```powershell
.\gradlew.bat -p migration :shared:desktopTest :composeApp:compileKotlinDesktop --console=plain
.\gradlew.bat -p migration :composeApp:run
```

## 免费 macOS CI

公开仓库使用标准 `macos-latest` runner。验证工作流会：

1. 运行原业务逻辑在 JVM 和 iOS 模拟器上的定向测试；
2. 编译共享 Compose UI 为 iOS framework；
3. 使用 XcodeGen 生成未签名的模拟器 App；
4. 检查付款页、充值玻璃弹窗、导航和深色模式；
5. 上传模拟器截图、测试报告和模拟器 App，产物保留 7 天。

模拟器 App 不能直接安装在 iPhone 上。此验证不需要 Apple Developer Program 账号。

## 目前的边界

- 全部页面使用虚构数据；验证工程没有业务 HTTP 客户端，也不会登录或发起支付。
- QR 内容是 `example.invalid`，条形码包含 `DEMO`，均不可用于付款。
- 还没有迁移真实扫码、支付宝/银联、Keystore/Keychain、付款码生命周期和截图监听。
- 原模糊代码保留能力检测和降级逻辑；本轮已检查模拟器的付款页及玻璃弹窗截图。
- iOS 的界面深色模式同时更新 UIKit 窗口样式，使系统栏和原组件的模式判断保持一致。

## 合并策略

此独立仓库保留原项目 Git 历史。后续以迁移实现提交作为合并起点，保持 Android 默认构建路径不受影响。
最终产品迁移应把已验证的源文件正式放入共享模块，并为平台能力定义明确的接口。
