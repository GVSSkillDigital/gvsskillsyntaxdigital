$bytes = New-Object byte[] 32
$rng = [System.Security.Cryptography.RandomNumberGenerator]::Create()
$rng.GetBytes($bytes)

$jwtSecret = [Convert]::ToBase64String($bytes)

setx JWT_SECRET "$jwtSecret"

$env:JWT_SECRET = $jwtSecret

Write-Host "JWT_SECRET configured successfully"