# scripts/extract-how2use.ps1
# Auto-generates HOW2USE.md from :ui component sources and :app/examples/ call patterns.
#
# For each component in ui/.../components/Xxx.kt (excluding Utils.kt) that has a
# matching Xxx*Example.kt in app/.../examples/, emit a markdown section with:
#   - one-line description from the component's KDoc
#   - the public fun Xxx(...) signature (with default values, all overloads)
#   - a Parameters table sourced from the KDoc's @param lines
#   - the full body of the matching example file (minus package + imports, capped at 60 lines)
#
# Run from the repo root:
#   powershell -ExecutionPolicy Bypass -File scripts/extract-how2use.ps1

$ErrorActionPreference = 'Stop'

# --- Paths ---
$repoRoot = Split-Path -Parent $PSScriptRoot
$componentsDir = Join-Path $repoRoot 'ui\src\commonMain\kotlin\com\composables\ui\components'
$examplesDir = Join-Path $repoRoot 'examples\src\main\kotlin\dev\dac114514\starter\examples'
$outputFile = Join-Path $repoRoot 'HOW2USE.md'

if (-not (Test-Path $componentsDir)) { throw "Components dir not found: $componentsDir" }
if (-not (Test-Path $examplesDir)) { throw "Examples dir not found: $examplesDir" }

# --- Discover files ---
$componentFiles = Get-ChildItem -Path $componentsDir -Filter '*.kt' |
    Where-Object { $_.Name -ne 'Utils.kt' } |
    Sort-Object Name

$exampleFiles = Get-ChildItem -Path $examplesDir -Filter '*.kt' |
    Sort-Object Name

# --- Helper: pick the best-matching example file for a component ---
# Matching rule (in priority order):
#   1. Exact stem match        (Button     <-> ButtonExample)
#   2. Stem starts with name   (BottomSheet<-> BottomSheetFormExample, etc.)
#   3. Name is a substring     (TextField  <-> DefaultTextFieldExample, etc.)
#   Bonus: stems starting with "Default" (the most basic variant) get a small bump.
#   Final tiebreakers: shorter stem, then alphabetical stem.
function Find-BestExample {
    param(
        [Parameter(Mandatory)][string]$ComponentName,
        [Parameter(Mandatory)][array]$Examples
    )

    $candidates = @()
    foreach ($ex in $Examples) {
        $stem = $ex.BaseName
        if ($stem -notmatch 'Example$') { continue }
        $variant = $stem -replace 'Example$', ''
        if ([string]::IsNullOrEmpty($variant)) { continue }

        $score = 0
        if ($variant -eq $ComponentName) {
            $score = 1000
        } elseif ($variant.StartsWith($ComponentName, [System.StringComparison]::OrdinalIgnoreCase)) {
            $score = 100
        } elseif ($variant.IndexOf($ComponentName, [System.StringComparison]::OrdinalIgnoreCase) -ge 0) {
            $score = 10
        }

        if ($score -gt 0) {
            if ($variant.StartsWith('Default', [System.StringComparison]::OrdinalIgnoreCase)) {
                $score += 5
            }
            $candidates += [PSCustomObject]@{
                Score = $score
                StemLength = $variant.Length
                Stem = $variant
                File = $ex
            }
        }
    }

    if ($candidates.Count -eq 0) { return $null }

    $best = $candidates |
        Sort-Object -Property @{Expression='Score'; Descending=$true},
                              @{Expression='StemLength'; Descending=$false},
                              @{Expression='Stem'; Descending=$false} |
        Select-Object -First 1
    return $best.File
}

# --- Helper: extract a single function signature (may span multiple lines) ---
# Starts at $StartIndex (which must be the `fun Xxx(` line) and reads forward
# counting parentheses until balanced, then strips the trailing `{` / `=` + body.
function Get-FunctionSignature {
    param(
        [Parameter(Mandatory)][array]$Lines,
        [Parameter(Mandatory)][int]$StartIndex
    )

    $buf = New-Object System.Collections.Generic.List[string]
    $parenDepth = 0
    $started = $false
    $closed = $false

    for ($idx = $StartIndex; $idx -lt $Lines.Count; $idx++) {
        $line = $Lines[$idx]
        $buf.Add($line)
        foreach ($ch in $line.ToCharArray()) {
            if ($ch -eq '(') { $parenDepth++; $started = $true }
            elseif ($ch -eq ')') { $parenDepth-- }
        }
        if ($started -and $parenDepth -le 0) {
            $closed = $true
            $lastLine = $buf[$buf.Count - 1]
            if ($lastLine -notmatch '^\s*\)\s*$') {
                # Closing paren shares a line with other content (e.g. `: ReturnType {`).
                # Strip from the first `{` or `=` that follows the `)`.
                $cutIdx = -1
                for ($i = 0; $i -lt $lastLine.Length; $i++) {
                    $c = $lastLine[$i]
                    if ($c -eq '{' -or $c -eq '=') { $cutIdx = $i; break }
                }
                if ($cutIdx -gt 0) {
                    $buf[$buf.Count - 1] = $lastLine.Substring(0, $cutIdx).TrimEnd()
                }
            }
            break
        }
    }

    if (-not $closed -or $buf.Count -eq 0) { return $null }
    $normalized = $buf | ForEach-Object { $_.TrimStart() }
    return ($normalized -join "`n")
}

# --- Helper: parse a component file ---
# Scans for KDoc blocks (`/** ... */`) that immediately precede a public
# `fun Xxx(...)` declaration (allowing for annotations in between).
# Returns: { Name, Description, Signatures, Params }.
function Parse-ComponentFile {
    param(
        [Parameter(Mandatory)][System.IO.FileInfo]$File
    )

    $lines = Get-Content -LiteralPath $File.FullName -Encoding UTF8
    $componentName = $File.BaseName
    $description = $null
    $signatures = New-Object System.Collections.Generic.List[string]
    $params = New-Object System.Collections.Generic.List[object]
    $paramsCollected = $false

    for ($i = 0; $i -lt $lines.Count; $i++) {
        $line = $lines[$i]
        if ($line -notmatch '^\s*/\*\*\s*$') { continue }

        # Collect KDoc lines.
        $kdocLines = New-Object System.Collections.Generic.List[string]
        $j = $i
        while ($j -lt $lines.Count -and $lines[$j] -notmatch '\*/') {
            $kdocLines.Add($lines[$j])
            $j++
        }
        if ($j -ge $lines.Count) { continue }

        # Look ahead past `*/` and annotations/blank lines for `fun Xxx(`.
        $k = $j + 1
        while ($k -lt $lines.Count) {
            $next = $lines[$k]
            if ($next -match "^\s*fun\s+$([regex]::Escape($componentName))\s*\(") { break }
            if ($next -match '^\s*@' -or $next -match '^\s*$') { $k++; continue }
            break
        }
        if ($k -ge $lines.Count) { $i = $j; continue }
        if ($lines[$k] -notmatch "^\s*fun\s+$([regex]::Escape($componentName))\s*\(") {
            $i = $j; continue
        }

        # Description: first non-empty content line of the KDoc.
        if (-not $description) {
            foreach ($kl in $kdocLines) {
                $clean = $kl -replace '^\s*\*\s?', ''
                $clean = $clean.Trim()
                if ($clean -and $clean -ne '/**') {
                    $description = $clean
                    break
                }
            }
        }

        # @param lines (from the first KDoc only — subsequent overloads don't add rows).
        if (-not $paramsCollected) {
            foreach ($kl in $kdocLines) {
                if ($kl -match '^\s*\*\s*@param\s+(\S+)\s*(.*)$') {
                    $pname = $Matches[1]
                    $pdesc = $Matches[2].Trim()
                    if ($pdesc.Length -gt 0) {
                        $params.Add([PSCustomObject]@{ Name = $pname; Description = $pdesc })
                    }
                }
            }
            $paramsCollected = $true
        }

        # Signature (every overload).
        $sig = Get-FunctionSignature -Lines $lines -StartIndex $k
        if ($sig) { $signatures.Add($sig) }

        $i = $k
    }

    return [PSCustomObject]@{
        Name = $componentName
        Description = $description
        Signatures = $signatures
        Params = $params
    }
}

# --- Helper: strip package/imports and cap at 60 lines ---
function Format-Example {
    param(
        [Parameter(Mandatory)][System.IO.FileInfo]$File
    )
    $lines = Get-Content -LiteralPath $File.FullName -Encoding UTF8
    $body = $lines | Where-Object {
        $t = $_.TrimStart()
        -not ($t -match '^package\s' -or $t -match '^import\s')
    }
    $maxLines = 60
    if ($body.Count -gt $maxLines) {
        $kept = $body[0..($maxLines - 1)]
        $relPath = $File.FullName.Replace($repoRoot, '').Replace('\', '/').TrimStart('/')
        $truncNote = "// … truncated; full example at $relPath"
        return (($kept -join "`n") + "`n" + $truncNote)
    }
    return ($body -join "`n")
}

# --- Main ---

$header = @'
# HOW2USE — composables-ui call patterns

This file is auto-generated by `scripts/extract-how2use.ps1`.
Re-run that script after pulling new upstream versions of `:ui` or `:app/examples`.

Each section is one component. The `Signature` is the ground-truth function signature
from `:ui` source. The `Example` is the real call pattern from `:app/examples/`.

If a section looks wrong, fix the source and re-run the script — do not edit this file by hand.
'@

$sections = New-Object System.Collections.Generic.List[string]
$usedExamples = New-Object System.Collections.Generic.HashSet[string]
$includedComponents = New-Object System.Collections.Generic.List[string]

foreach ($cf in $componentFiles) {
    $parsed = Parse-ComponentFile -File $cf
    $example = Find-BestExample -ComponentName $parsed.Name -Examples $exampleFiles
    if (-not $example) { continue }

    $usedExamples.Add($example.Name) | Out-Null
    $includedComponents.Add($parsed.Name) | Out-Null

    $sec = New-Object System.Collections.Generic.List[string]
    [void]$sec.Add("## $($parsed.Name)")
    [void]$sec.Add("")
    if ($parsed.Description) {
        [void]$sec.Add($parsed.Description)
        [void]$sec.Add("")
    }

    [void]$sec.Add("### Signature")
    [void]$sec.Add("")
    [void]$sec.Add('```kotlin')
    foreach ($sig in $parsed.Signatures) {
        [void]$sec.Add($sig)
        [void]$sec.Add("")
    }
    [void]$sec.Add('```')
    [void]$sec.Add("")

    if ($parsed.Params.Count -gt 0) {
        [void]$sec.Add("### Parameters")
        [void]$sec.Add("")
        [void]$sec.Add("| name | description |")
        [void]$sec.Add("| --- | --- |")
        foreach ($p in $parsed.Params) {
            $name = $p.Name.Replace('|', '\|')
            $desc = $p.Description.Replace('|', '\|')
            [void]$sec.Add("| ``$name`` | $desc |")
        }
        [void]$sec.Add("")
    }

    [void]$sec.Add("### Example")
    [void]$sec.Add("")
    [void]$sec.Add('```kotlin')
    $exampleText = Format-Example -File $example
    [void]$sec.Add($exampleText)
    [void]$sec.Add('```')
    [void]$sec.Add("")

    $sections.Add(($sec -join "`n"))
}

# Warn about example files with no matching component.
foreach ($ex in $exampleFiles) {
    if (-not $usedExamples.Contains($ex.Name)) {
        [Console]::Error.WriteLine("WARN: example file has no matching component: $($ex.Name)")
    }
}

$body = $sections -join "`n"
$full = $header + "`n`n" + $body
Set-Content -LiteralPath $outputFile -Value $full -Encoding UTF8 -NoNewline

Write-Host "Wrote $outputFile"
Write-Host "Sections: $($sections.Count)"
Write-Host "Components included: $($includedComponents -join ', ')"
