/**
 * @param str {*}
 * @return {Promise<*>}
 */
function PromiseSupplier(str) {
    return new Promise(a => a())
}