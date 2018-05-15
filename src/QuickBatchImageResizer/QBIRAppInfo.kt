package QuickBatchImageResizer

import LatteFX.*

/**
 * @author ben
 * @since 5/13/18.
 */
object QBIRAppInfo: LatteAppInfo {
    override val appName = "Quick Batch Image Resizer"
}


object QBIRAppConfig: LatteAppConfig {
    override val startingWrapperGenerator = { MainComponentWrapper() }
    override val automaticallySetUpSystemMenuBar = true
    override val primaryGroup: LatteGroup? = null
}
