# !/bin/bash
echo -e "Build Fabric Network..."
cd hyperledgerFabric/network 
./build.sh

echo -e "Run Backend Server..."
cd ..
java -jar NAC_Service_HyperledgerFabric.jar &

echo -e "Run Front Server..."
cd ../frontend
npm run dev:server &