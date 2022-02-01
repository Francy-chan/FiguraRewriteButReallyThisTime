-- Library for managing modules in figura avatars
local figura_modules = {

}

--- // LIBRARIES // ---

--List of all current events
local loadedLibraries = {}

--Returns or loads a script file, by name.
--These scripts are the ones stored with the avatar.
function figura_modules.require(libName)
    local existingLibrary = loadedLibraries[libName]

    --If no library exists, load from scripts.
    if existingLibrary == nil then
        --Load from java-side function.
        local scriptSource = f_loadScript(libName)

        existingLibrary = sandbox.run(scriptSource, {env = scriptEnvironment})

        --Put in loaded library list
        loadedLibraries[libName] = existingLibrary
    end

    return existingLibrary
end

--- // EVENTS // ---

--Calls an event on all libraries.
function figura_modules.callEvent(eventName, ...)
    for key,lib in pairs(loadedLibraries) do
        --Get value from library.
        local eventFunction = lib[eventName]

        --If value is a function, call it.
        if type(eventFunction) == "function" then
            --Pass in self as first argument
            eventFunction(lib, ...)
        end
    end
end


return figura_modules