local avatar_module = {



}

-- // SANDBOX SETUP // --

local f_print = f_print
local f_logPrints = f_logPrints
local sandbox = sandbox
local debug = debug

-- Set up print function customization
function print(...)
    local count = select('#', ...)

    for i = 1, count do
        f_print(tostring(select(i, ...)))
    end

    f_logPrints()
end

--Custom pcall that ignores errors related to instruction limit
local true_pcall = pcall
local ignore_protection = false --Defaults to false. When true, pcall can no longer catch exceptions.
local error_message = nil

function pcall(...)
    local tbl = table.pack(true_pcall(...))

    if tbl[1] then
        --Function ran correctly
        return table.unpack(tbl) -- Return values, behave like normal.
    else
        -- Function errored

        --If the error was an instruction count error, don't protect against this.
        if ignore_protection then
            error(error_message, 2)
        else
            --If the error wasn't an instruction error, behave like normal.
            return table.unpack(tbl)
        end
    end
end

function avatar_module.resetInstructionWatcher()
    debug.sethook()
end

function avatar_module.setInstructionWatcher(callback, count)
    debug.sethook(
            function()
                ignore_protection = true --Set this so that pcall knows to ignore this error and stop protecting functions
                error_message = 'Instruction limit hit for function "' .. callback() .. '"'
                error()
            end,
            "",
            count
    )
end

scriptSandbox._Grandpa = scriptSandbox -- <3

sandbox.setEnv(scriptSandbox)

--Clean up global values. Only keep what's absolutely required.
for k, v in pairs(_G) do
    if k ~= "_G" and k ~= "scriptSandbox" and scriptSandbox[k] == nil then
        _G[k] = nil
    end
end

return avatar_module