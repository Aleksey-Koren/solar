/**
 * @param arg {*}
 * @return {Promise<*>}
 */
function PromiseSupplier(arg) {
    return new Promise(a => a())
}