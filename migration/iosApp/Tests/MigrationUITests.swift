import XCTest

final class MigrationUITests: XCTestCase {
    private func navigationButton(_ title: String, in app: XCUIApplication) -> XCUIElement {
        // Miuix beta combines the icon description and visible text in iOS accessibility.
        app.buttons.matching(NSPredicate(format: "label BEGINSWITH %@", title)).firstMatch
    }
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

        navigationButton("我的", in: app).tap()
        XCTAssertTrue(app.staticTexts["应用设置"].waitForExistence(timeout: 10))
        capture("settings-light")
        app.buttons["切换深色"].tap()
        XCTAssertTrue(app.buttons["切换浅色"].waitForExistence(timeout: 10))
        navigationButton("付款", in: app).tap()
        XCTAssertTrue(app.staticTexts["付款码"].waitForExistence(timeout: 10))
        capture("payment-dark")
    }
}
