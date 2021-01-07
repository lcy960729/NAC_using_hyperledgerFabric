# !/bin/bash
echo -e "Stop Backend Server..."
pkill -9 -f NAC_Service_HyperledgerFabric

echo -e "Stop Front Server..."
killall node

echo -e "Teardown Fabric Network..."
cd hyperledgerFabric/network 
 ./teardown.sh

