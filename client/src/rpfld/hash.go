package rpfld

import (
	"crypto/sha512"
	"io/ioutil"
	"time"
	"github.com/pmylund/go-cache"
)

var hashCache = cache.New(5 * time.Minute, 30*time.Second)

func prepare(path *string){
	hash, err := hashLocal(path)

	if err != nil {
		panic(err)
	}

	hashCache.Set(*path, hash, cache.DefaultExpiration)
}

func hashLocal(path *string) (hash []byte, err error){
	if hash, found := hashCache.Get(*path); found {
		return hash.([]byte), nil
	}

	digest := sha512.New()

	bytes, err:= ioutil.ReadFile(*path);

	if err != nil{
		return nil, err
	}

	return digest.Sum(bytes), nil;
}

func calculateHash(downloadedResources []DownloadedResource, payload []byte)(calculatedHash []byte){
	digest := sha512.New()

	finalIndex := len(downloadedResources) - 1

	digest.Write(payload)

	for ind, downloadedResource := range downloadedResources {
		hash, err := hashLocal(downloadedResource.path)

		if err != nil {
			panic(err)
		}

		isLastResource := finalIndex == ind

		if isLastResource {
			calculatedHash = digest.Sum(hash)
		} else {
			digest.Write(hash)
		}
	}

	return calculatedHash
}