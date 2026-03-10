// vm.bicep - Virtual Machine (Ubuntu 22.04 LTS, password auth, cloud-init)

@description('Azure region')
param location string

@description('Resource tags')
param tags object

@description('VM name')
param vmName string

@description('VM size')
param vmSize string

@description('Admin username')
param adminUsername string

@secure()
@description('Admin password')
param adminPassword string

@description('NIC resource ID')
param networkInterfaceId string

@description('base64-encoded cloud-init user data')
param customData string

resource vm 'Microsoft.Compute/virtualMachines@2023-09-01' = {
  name: vmName
  location: location
  tags: tags
  properties: {
    hardwareProfile: {
      vmSize: vmSize
    }
    osProfile: {
      computerName: vmName
      adminUsername: adminUsername
      adminPassword: adminPassword
      customData: customData
      linuxConfiguration: {
        disablePasswordAuthentication: false
        provisionVMAgent: true
      }
    }
    storageProfile: {
      imageReference: {
        publisher: 'Canonical'
        offer: '0001-com-ubuntu-server-jammy'
        sku: '22_04-lts-gen2'
        version: 'latest'
      }
      osDisk: {
        name: 'osdisk-${vmName}'
        caching: 'ReadWrite'
        createOption: 'FromImage'
        managedDisk: {
          storageAccountType: 'Standard_LRS'
        }
        diskSizeGB: 30
      }
    }
    networkProfile: {
      networkInterfaces: [
        {
          id: networkInterfaceId
        }
      ]
    }
  }
}

output vmId string = vm.id
