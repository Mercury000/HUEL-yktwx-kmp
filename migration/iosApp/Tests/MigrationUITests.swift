import XCTest

final class MigrationUITests: XCTestCase {
    private func capture(_ name: String) {
        let attachment = XCTAttachment(screenshot: XCUIScreen.main.screenshot())
        attachment.name = name
        attachment.lifetime = .keepAlways
        add(attachment)
    }

    func testPaymentAndGlassDialog() {
        continueAfterFailure = false
        let app = XCUIApplication()
        app.launch()
        XCTAssertTrue(app.staticTexts["付款码"].waitForExistence(timeout: 30))
        XCTAssertTrue(app.staticTexts["128.50"].exists)
        capture("payment-light")

        app.buttons["充值"].tap()
        XCTAssertTrue(app.staticTexts["本人充值"].waitForExistence(timeout: 10))
        XCTAssertTrue(app.staticTexts["仅使用虚构数据，不会发起真实支付"].exists)
        capture("recharge-glass-dialog")
        app.buttons["关闭"].tap()

        app.buttons["我的"].tap()
        XCTAssertTrue(app.staticTexts["应用设置"].waitForExistence(timeout: 10))
        capture("settings-light")
        app.buttons["切换深色"].tap()
        app.buttons["付款"].tap()
        XCTAssertTrue(app.staticTexts["付款码"].waitForExistence(timeout: 10))
        capture("payment-dark")
    }
}
