package QuickBatchImageResizer

import LatteFX.*

/**
 * @author ben
 * @since 5/12/18.
 */
//fun main(args: Array<String>) {
//    MainWindow().isVisible = true
//}



class Main: LatteFXMain(appInfo = QBIRAppInfo,
                        appConfig = QBIRAppConfig,
                        onStart = { commandLineArguments, primaryWindow ->
    if (null != primaryWindow) {
        primaryWindow.show()
    }
})
