#docker volume create --driver local \
#      --opt type=none \
#      --opt device=/home/marvin/src/github.com/dbpedia/databus-mods/databus-mods-server/volumes/fileCache \
#      --opt o=bind \
#      DMFileCache

#docker volume create --driver local \
#      --opt type=none \
#      --opt device=/home/marvin/src/github.com/dbpedia/databus-mods/databus-mods-server/volumes/VoidVocab \
#      --opt o=bind \
#      VoidVocabLR

docker volume create --driver local \
      --opt type=none \
      --opt device=/home/marvin/src/github.com/dbpedia/databus-mods/databus-mods-server/volumes/FileMetrics \
      --opt o=bind \
      FileMetricsLR

