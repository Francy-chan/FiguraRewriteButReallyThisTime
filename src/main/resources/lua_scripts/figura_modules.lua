-- Library for managing modules in figura avatars
local figura_modules = {

}

--- // Variables // ---
local loadedModules = {} --List of all loaded modules
local eventList = {} --Of all used events, indexed by event name


--- // EVENTS // ---
---
local function getOrConstructEvent(eventName)
    local events = eventList[eventName]

    --If no event list is found, construct one
    if events == nil then
        --Create new table
        local newEventTable = {}

        for key, module in pairs(loadedModules) do
            --Get value from library.
            local eventFunction = module[eventName]

            if type(eventFunction) == "function" then
                --Create wrapper that stores module
                local function eventTrue(...)
                    eventFunction(module, ...)
                end

                --Add to list of events by this name
                table.insert(newEventTable, eventTrue)
            end
        end

        --Set table to event list
        eventList[eventName] = newEventTable
        events = newEventTable
    end

    return events
end

--Calls an event on all libraries.
function figura_modules.callEvent(eventName, ...)
    --Get existing list of events
    local events = getOrConstructEvent(eventName)

    print("Calling event " .. eventName)

    --Call all events by this name
    for i, e in pairs(events) do
        e(...)
    end
end

--Constructs and provides a function that will simply call the given event with as little overhead as possible
function figura_modules.constructEventFunction(eventName)
    local events = getOrConstructEvent(eventName)

    print("Constructing event function for " .. eventName)

    --Return a function that just takes the arguments and runs over the table provided.
    return function(...)
        for i, e in pairs(events) do
            e(...)
        end
    end
end

--- // MODULES // ---

--Returns or loads a script file, by name.
--These scripts are the ones stored with the avatar.
function figura_modules.require(moduleName)
    local existingModule = loadedModules[moduleName]

    --If no module exists, load from scripts.
    if existingModule == nil then
        print("Loading module " .. moduleName)
        --Load from java-side function.
        local scriptSource = f_loadScript(moduleName)

        existingModule = sandbox.run(scriptSource, { env = scriptEnvironment })

        --Register events from module
        for eventName, eventList in pairs(eventList) do
            local eventFunction = existingModule[eventName]

            if type(eventFunction) == "function" then
                --Create wrapper that stores lib
                local function eventTrue(...)
                    eventFunction(existingModule, ...)
                end

                --Add to list of events by this name
                table.insert(eventList, eventTrue)
            end
        end

        --Put in loaded library list
        loadedModules[moduleName] = existingModule
    end

    return existingModule
end

return figura_modules