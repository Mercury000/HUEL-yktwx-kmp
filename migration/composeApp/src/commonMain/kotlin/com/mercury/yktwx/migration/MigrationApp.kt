package com.mercury.yktwx.migration

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.mercury.yktwx.migration.resources.Res
import com.mercury.yktwx.migration.resources.demo_barcode
import com.mercury.yktwx.migration.resources.demo_qr
import com.mercury.yktwx.ui.components.*
import org.jetbrains.compose.resources.painterResource
import top.yukonga.miuix.kmp.basic.*
import top.yukonga.miuix.kmp.icon.MiuixIcons
import top.yukonga.miuix.kmp.icon.extended.Scan
import top.yukonga.miuix.kmp.theme.MiuixTheme
import top.yukonga.miuix.kmp.theme.darkColorScheme
import top.yukonga.miuix.kmp.theme.lightColorScheme

/** UI-only probe. There is no account service, permission request or payment SDK in this app. */
@Composable
fun MigrationApp() {
    var dark by remember { mutableStateOf(false) }
    var glass by remember { mutableStateOf(true) }
    var selectedTab by remember { mutableStateOf("付款") }
    var recharge by remember { mutableStateOf(false) }
    var enlarged by remember { mutableStateOf(false) }
    var scanNotice by remember { mutableStateOf(false) }
    MiuixTheme(colors = if (dark) darkColorScheme() else lightColorScheme()) {
        AppAppearanceHost(glassStyle = glass) {
            BarBlurHost(bottomBarBlurEnabled = true, topBarBlurEnabled = true) {
                Scaffold(
                    topBar = {
                        BlurredBar(topGradient = true) {
                            TopAppBar(title = selectedTab, actions = {
                                if (selectedTab == "付款") IconButton(onClick = { scanNotice = true },
                                    modifier = Modifier.padding(end = 16.dp)) {
                                    Icon(MiuixIcons.Scan, "扫一扫", Modifier.size(24.dp))
                                }
                            })
                        }
                    },
                    bottomBar = {
                        BlurredBar {
                            Row(Modifier.fillMaxWidth().navigationBarsPadding().padding(horizontal = 12.dp, vertical = 8.dp),
                                horizontalArrangement = Arrangement.SpaceEvenly) {
                                listOf("付款", "大厅", "账单", "我的").forEach { tab ->
                                    TextButton(tab, onClick = { selectedTab = tab },
                                        modifier = Modifier.weight(1f),
                                        colors = if (selectedTab == tab) ButtonDefaults.textButtonColorsPrimary()
                                            else ButtonDefaults.textButtonColors())
                                }
                            }
                        }
                    },
                ) { padding ->
                    AppearanceBackdropContent {
                        BarBackdropContent(Modifier.fillMaxSize().background(MiuixTheme.colorScheme.surface)) {
                            LazyColumn(Modifier.fillMaxSize(),
                                contentPadding = PaddingValues(start = 16.dp, end = 16.dp,
                                    top = padding.calculateTopPadding() + 8.dp,
                                    bottom = padding.calculateBottomPadding() + 28.dp),
                                verticalArrangement = Arrangement.spacedBy(16.dp)) {
                                when (selectedTab) {
                                    "付款" -> {
                                        item { PaymentPreview(onRecharge = { recharge = true }, onEnlarge = { enlarged = true }) }
                                        item {
                                            Text("虚构数据 · 付款码不可用于支付", Modifier.fillMaxWidth(),
                                                fontSize = 12.sp, textAlign = TextAlign.Center,
                                                color = MiuixTheme.colorScheme.onSurfaceVariantSummary)
                                        }
                                    }
                                    "我的" -> item {
                                        Card(Modifier.fillMaxWidth(), cornerRadius = 24.dp) {
                                            Column(Modifier.padding(20.dp), verticalArrangement = Arrangement.spacedBy(16.dp)) {
                                                Text("应用设置", fontSize = 20.sp, fontWeight = FontWeight.SemiBold)
                                                TextButton(if (dark) "切换浅色" else "切换深色", onClick = { dark = !dark })
                                                TextButton(if (glass) "关闭玻璃效果" else "开启玻璃效果", onClick = { glass = !glass })
                                            }
                                        }
                                    }
                                    else -> item {
                                        Card(Modifier.fillMaxWidth(), cornerRadius = 24.dp) {
                                            Column(Modifier.padding(20.dp)) {
                                                Text(selectedTab, fontSize = 20.sp)
                                                Text("本阶段仅验证付款页面与系统适配边界", fontSize = 13.sp)
                                            }
                                        }
                                    }
                                }
                            }
                        }
                    }
                }
                AppOverlayDialog(show = recharge, title = "本人充值",
                    summary = "仅使用虚构数据，不会发起真实支付", onDismissRequest = { recharge = false }) {
                    AppTextButton("关闭", onClick = { recharge = false }, modifier = Modifier.fillMaxWidth())
                }
                AppOverlayDialog(show = enlarged, title = "付款二维码", onDismissRequest = { enlarged = false }) {
                    Image(painterResource(Res.drawable.demo_qr), "放大的验证二维码",
                        Modifier.fillMaxWidth().aspectRatio(1f), contentScale = ContentScale.Fit)
                    AppTextButton("收起", onClick = { enlarged = false }, modifier = Modifier.fillMaxWidth())
                }
                AppOverlayDialog(show = scanNotice, title = "扫一扫",
                    summary = "相机与扫码识别需要 iOS 平台实现，本阶段仅验证界面", onDismissRequest = { scanNotice = false }) {
                    AppTextButton("关闭", onClick = { scanNotice = false }, modifier = Modifier.fillMaxWidth())
                }
            }
        }
    }
}

/** Normal payment layout transcribed from upstream PaymentPage.kt at 5697ed0.
 * Dimensions, spacing, typography and the original number/icons components are preserved.
 * Android bitmap encoding is replaced by deliberately non-payment fixture images.
 */
@Composable
private fun PaymentPreview(onRecharge: () -> Unit, onEnlarge: () -> Unit) {
    Column(verticalArrangement = Arrangement.spacedBy(16.dp)) {
        Card(Modifier.fillMaxWidth().testTag("payment-code-card"), cornerRadius = 24.dp) {
            Column(Modifier.fillMaxWidth().padding(20.dp), horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.spacedBy(16.dp)) {
                Row(Modifier.fillMaxWidth().height(32.dp), verticalAlignment = Alignment.CenterVertically) {
                    Text("付款码", fontSize = 16.sp, lineHeight = 28.sp, fontWeight = FontWeight.SemiBold)
                    Spacer(Modifier.weight(1f))
                    Text("60s 后刷新", fontSize = 12.sp, lineHeight = 16.sp,
                        color = MiuixTheme.colorScheme.onSurfaceVariantSummary)
                }
                Column(Modifier.fillMaxWidth(), horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.spacedBy(16.dp)) {
                    Box(Modifier.fillMaxWidth().height(88.dp), contentAlignment = Alignment.Center) {
                        Image(painterResource(Res.drawable.demo_barcode), "验证条形码", Modifier.fillMaxSize(),
                            contentScale = ContentScale.FillBounds)
                    }
                    Box(Modifier.size(232.dp), contentAlignment = Alignment.Center) {
                        Image(painterResource(Res.drawable.demo_qr), "验证二维码",
                            Modifier.fillMaxSize().clickable(onClick = onEnlarge), contentScale = ContentScale.Fit)
                    }
                    PaymentCodeNumber("0000000000000000", isActive = true)
                    Text("向商户出示，切勿分享或截图", Modifier.fillMaxWidth(), textAlign = TextAlign.Center,
                        fontSize = 13.sp, lineHeight = 18.sp, color = MiuixTheme.colorScheme.onSurfaceVariantSummary)
                }
            }
        }
        Card(Modifier.fillMaxWidth(), cornerRadius = 24.dp) {
            Column(Modifier.padding(horizontal = 20.dp, vertical = 16.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
                Row(Modifier.fillMaxWidth(), verticalAlignment = Alignment.CenterVertically) {
                    Icon(AppIcons.CampusCard, null, Modifier.size(14.dp), tint = MiuixTheme.colorScheme.onSurfaceVariantSummary)
                    Text("统一账户", Modifier.padding(start = 6.dp).weight(1f), fontSize = 11.sp,
                        color = MiuixTheme.colorScheme.onSurfaceVariantSummary)
                }
                Row(Modifier.fillMaxWidth(), verticalAlignment = Alignment.CenterVertically) {
                    Text("¥ ", fontSize = 30.sp, fontWeight = FontWeight.SemiBold)
                    Text("128.50", Modifier.weight(1f), fontSize = 30.sp, fontWeight = FontWeight.SemiBold)
                    TextButton("充值", onClick = onRecharge)
                }
            }
        }
    }
}
