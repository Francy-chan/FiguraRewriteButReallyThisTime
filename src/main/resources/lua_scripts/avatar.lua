local avatar_module = {



}

-- // SANDBOX SETUP // --

-- Set up print function customization
function print(...)
    local count = select('#', ...)

    for i = 1, count do
        f_print(tostring(select(i, ...)))
    end

    f_logPrints()
end


-- Load up the sandbox lua script
sandbox = f_loadRes("sandbox")
figura_modules = f_loadRes("figura_modules")
require = figura_modules.require


scriptEnvironment = {
    _Grandpa = scriptEnvironment, -- <3
    require = require,
    string = string,
    print = print,
    figura = figura,
    pairs = pairs,
    ipairs = ipairs,
}

-- Relay these values so they're easier to access
avatar_module.callEvent = figura_modules.callEvent
avatar_module.constructEventFunction = figura_modules.constructEventFunction

return avatar_module