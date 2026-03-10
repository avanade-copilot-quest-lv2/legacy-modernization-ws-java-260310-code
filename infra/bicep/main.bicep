// main.bicep - Legacy Bookstore: subscription-scoped entry point
// Deploys: Resource Group → VNet/NSG/NIC/PublicIP → Ubuntu VM with cloud-init
targetScope = 'subscription'

@description('Azure region for all resources')
param location string = 'japaneast'

@description('Resource group name')
param resourceGroupName string = 'rg-legacy-bookstore'

@description('VM name')
param vmName string = 'vm-legacy-bookstore'

@description('VM size')
param vmSize string = 'Standard_D2s_v3'

@description('Admin username for the VM')
param adminUsername string = 'azureuser'

@secure()
@minLength(12)
@description('Admin password for the VM (12+ chars, uppercase + lowercase + digit + special char)')
param adminPassword string

@description('Resource tags')
param tags object = {
  project: 'legacy-bookstore'
  environment: 'dev'
}

@description('base64-encoded cloud-init user data. Generate with: [Convert]::ToBase64String([System.IO.File]::ReadAllBytes("infra/scripts/cloud-init.yaml"))')
param customData string = ''

resource rg 'Microsoft.Resources/resourceGroups@2023-07-01' = {
  name: resourceGroupName
  location: location
  tags: tags
}

module network 'modules/network.bicep' = {
  scope: rg
  params: {
    location: location
    tags: tags
    vmName: vmName
  }
}

module vm 'modules/vm.bicep' = {
  scope: rg
  params: {
    location: location
    tags: tags
    vmName: vmName
    vmSize: vmSize
    adminUsername: adminUsername
    adminPassword: adminPassword
    networkInterfaceId: network.outputs.networkInterfaceId
    customData: customData
  }
}

output publicIpAddress string = network.outputs.publicIpAddress
output resourceGroupName string = rg.name
output sshCommand string = 'ssh ${adminUsername}@${network.outputs.publicIpAddress}'
output appUrl string = 'http://${network.outputs.publicIpAddress}:8080/legacy-app/'
