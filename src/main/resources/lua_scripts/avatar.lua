local avatar_container = {

}

-- // SANDBOX SETUP // --

-- Load up the sandbox lua script
sandbox = f_loadRes("sandbox")
figura_modules = f_loadRes("figura_modules")
require = figura_modules.require

scriptEnvironment = {
    require = require,
    string = string,
    print = print,
}


-- Relay this value so it's easier to access
avatar_container.callEvent = figura_modules.callEvent

return avatar_container