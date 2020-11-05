#!/bin/bash
#
# Exit on first error, print all commands.
set -e

#Start from here
echo -e "\nGenerate crypto_config, genesis.block, channel.tx"
../bin/cryptogen generate --config=./crypto-config.yaml
export FABRIC_CFG_PATH=$PWD

mkdir config
../bin/configtxgen -profile TwoOrgsOrdererGenesis -channelID byfn-sys-channel -outputBlock ./config/genesis.block
export CHANNEL_NAME=usernetwork && ../bin/configtxgen -profile TwoOrgsChannel -outputCreateChannelTx ./config/channel.tx -channelID $CHANNEL_NAME

rm -rf ../network_resources/config
mv config ../network_resources/config

rm -rf ../network_resources/crypto-config
mv crypto-config ../network_resources/crypto-config

echo -e "\nComplete!!\n"

