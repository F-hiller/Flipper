docker-compose down
rm -rf ./docker/data/kafka_data
docker-compose --env-file .env up -d