// dev.bicepparam - Development environment parameters for Legacy Bookstore VM
// Usage:
//   az deployment sub create \
//     --location japaneast \
//     --template-file infra/bicep/main.bicep \
//     --parameters infra/bicep/parameters/dev.bicepparam \
//                  adminPassword='<YOUR_SECURE_PASSWORD>'
//
// Note: adminPassword is intentionally omitted here (@secure parameter).
//       Pass it via --parameters adminPassword='...' or interactively when prompted.

using '../main.bicep'

param location           = 'japaneast'
param resourceGroupName  = 'rg-legacy-bookstore'
param vmName             = 'vm-legacy-bookstore'
param vmSize             = 'Standard_B2s'
param adminUsername      = 'azureuser'
param tags               = {
  project: 'legacy-bookstore'
  environment: 'dev'
}
