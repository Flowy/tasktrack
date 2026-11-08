param([string]$Javac = 'javac')
$ErrorActionPreference = 'Stop'
# MOD-001/002: compile probes against the actual production descriptors.
$root = $PSScriptRoot
$modules = @('api', 'client', 'server', 'app')
$modulePath = ($modules | ForEach-Object { Join-Path $root "$_/target/classes" }) -join [IO.Path]::PathSeparator
$gson = Join-Path $env:USERPROFILE '.m2/repository/com/google/code/gson/gson/2.13.1/gson-2.13.1.jar'
if (!(Test-Path -LiteralPath $gson)) { throw 'Build with Maven first to resolve Gson 2.13.1.' }
$modulePath += [IO.Path]::PathSeparator + $gson
$probeRoot = Join-Path $root ('target/boundary-check-' + [Guid]::NewGuid().ToString('N'))
New-Item -ItemType Directory -Force $probeRoot | Out-Null
foreach ($module in $modules) {
    $output = Join-Path $probeRoot "legal/$module"
    $sources = @(Get-ChildItem (Join-Path $root "$module/src/main/java") -Recurse -Filter *.java | ForEach-Object FullName)
    & $Javac --release 25 -encoding UTF-8 --module-path $modulePath -d $output @sources
    if ($LASTEXITCODE -ne 0) { throw "Legal $module compilation failed" }
}
$cases = @(
    @('client', 'server', 'InMemoryTracking', $false),
    @('server', 'client', 'MainWindow', $false),
    @('api', 'client', 'MainWindow', $false),
    @('api', 'server', 'InMemoryTracking', $false),
    @('client', 'server', 'InMemoryTracking', $true),
    @('server', 'client', 'MainWindow', $true),
    @('api', 'client', 'MainWindow', $true),
    @('api', 'server', 'InMemoryTracking', $true)
)
$index = 0
foreach ($case in $cases) {
    $from, $to, $type, $addRequires = $case
    $folder = Join-Path $probeRoot "probe-$index"
    New-Item -ItemType Directory -Force $folder | Out-Null
    $descriptor = Get-Content (Join-Path $root "$from/src/main/java/module-info.java") -Raw
    if ($addRequires) { $descriptor = $descriptor.Replace('{', "{`n requires tasktrack.$to;") }
    [IO.File]::WriteAllText((Join-Path $folder 'module-info.java'), $descriptor)
    $source = "package com.flowyk.tasktrack.$from; import com.flowyk.tasktrack.$to.$type; class Forbidden { $type value; }"
    [IO.File]::WriteAllText((Join-Path $folder 'Forbidden.java'), $source)
    $previousPreference = $ErrorActionPreference
    $ErrorActionPreference = 'Continue' # Windows PowerShell wraps expected javac stderr as errors.
    $diagnostics = & $Javac --release 25 --module-path $modulePath -d (Join-Path $folder 'classes') (Join-Path $folder 'module-info.java') (Join-Path $folder 'Forbidden.java') 2>&1
    $ErrorActionPreference = $previousPreference
    if ($LASTEXITCODE -eq 0) { throw "Forbidden edge compiled: $from -> $to" }
    if (($diagnostics -join "`n") -notmatch 'not visible|cyclic dependence') { throw "Unexpected compiler failure: $diagnostics" }
    Write-Output "PASS: $from -> $to rejected (extra requires: $addRequires)"
    $index++
}
Write-Output 'PASS: MOD-001/002 legal modules compile; all eight forbidden edges rejected.'
