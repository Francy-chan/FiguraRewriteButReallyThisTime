-- {{  Scripting Environment  }} --
--[[
    This is the section dedicated to maintaining the lua sandbox of an avatar by creating a safe scripting environment.

    Noteworthy functions left out of the normal lua sandbox include:
    pcall : Can be used to circumvent other safety protocols
    xpcall : Same reason
--]]

-- The environment variable for this script.
local script_env = {}

local function create_readonly(tbl)
    local proxy = {} --Proxy table to hold the metatable
    local mt = { --Metatable
        __index = tbl, -- Read directly from table
        __newindex = function(t, k, v)
            error("Attempt to update a read-only table", 2)
        end
    }
    setmetatable(proxy, mt)
    return proxy
end

-- CUSTOMIZED FUNCTIONS --

-- TODO - Replace!!!
function script_env.print(...)
    print(...)
end

---Loads an API using Figura's custom require implementation
function script_env.require(api)

end

---Loads another file from the avatar using Figura's custom load implementation
function script_env.load(path)
    return load_script(path)
end

-- DEFAULT FUNCTIONS --

--Debugging
script_env.assert = assert
script_env.error = error

--Iterators
script_env.ipairs = ipairs
script_env.next = next
script_env.pairs = pairs
script_env.select = select

--Conversions
script_env.tonumber = tonumber
script_env.tostring = tostring
script_env.type = type
script_env._VERSION = _VERSION

--Tables
script_env.coroutine = create_readonly(coroutine)
--TODO figure out what we can/can't use from string
script_env.table = create_readonly(table)
script_env.math = create_readonly(math)

-- {{ Scripts }} --
--[[
    This section is for managing scripts supplied from Java.

    Java-side functions :
    j_loadScript(string path) - Loads a script into a chunk by path and returns it, or returns false if none is found.
--]]

---List of scripts (as chunks) currently loaded in this lua state, indexed by their paths.
local scripts = {}

function load_script(path)
    local script = scripts[path]

    if script == false then --Check if script has already failed loading once
        return nil
    elseif script == nil then --Check if script is not loaded yet
        script = j_loadScript(path, script_env) --Attempt to load from java
        scrips[path] = script --Assign to table

        script() --Call the script to initialize it.
    end

    return script
end

load_script("test")

-- {{ APIS }} --
--[[
    This section is for managing the APIs supplied from Java.
--]]


-- {{ Events }} --
--[[
    This section is for managing script events that are called java-side.
--]]