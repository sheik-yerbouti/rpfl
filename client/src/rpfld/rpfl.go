package rpfld

import (
	"io/ioutil"
	"bytes"
	"net/http"
	"github.com/agl/ed25519"
	"sort"
	"github.com/satori/go.uuid"
	"github.com/pmylund/go-cache"
	"time"
)

var processCache = cache.New(5 * time.Minute, 30*time.Second)

func startProcess(downloadedResources []DownloadedResource) (processId string){

	sort.Sort(ByUrl(downloadedResources))

	urls := getUrls(downloadedResources)

	payload, err := createPayload(urls)

	if err != nil {
		panic(err)
	}

	verifications := make(chan Verification);

	for _, endpoint := range endpoints{
		go getVerification(&endpoint, payload, verifications)
	}

	verificationProcess := &VerificationProcess{
		payload: payload,
		verifications:verifications,
		downloadedResources: downloadedResources,
	};

	processId = uuid.NewV4().String();

	processCache.Add(processId, verificationProcess, cache.DefaultExpiration)

	return processId
}

func endProcess(processId *string)(verified bool) {

	var process VerificationProcess

	if p, found := processCache.Get(*processId); found {
		process = p.(VerificationProcess)
	} else {
		return false
	}

	calculatedHash := calculateHash(process.downloadedResources, process.payload)

	for _,_ = range endpoints{
		verification := <- process.verifications

		if !ed25519.Verify(&verification.endpoint.publicKey, calculatedHash, &verification.signature) {
			return false
		}
	}

	return true
}

func getVerification(endpoint *Endpoint, payload []byte, verifications chan Verification){
	response, err := http.Post(endpoint.url.String(), "", bytes.NewReader(payload))

	responseBody, err := ioutil.ReadAll(response.Body)

	response.Body.Close()

	if err != nil {
		panic(err)
	}

	if len(responseBody) != 64 {
		panic("expected responsebody from " + endpoint.url.String() + " to be 64 but was " + string(len(responseBody)))
	}

	var	signature [64]byte

	copy(signature[:], responseBody[0:64])

	verification := new(Verification)
	verification.endpoint = endpoint
	verification.signature = signature

	verifications <- *verification
}
