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
    _Grandpa = scriptEnvironment
}

-- Relay these values so they're easier to access
avatar_container.callEvent = figura_modules.callEvent
avatar_container.constructEventFunction = figura_modules.constructEventFunction

return avatar_container