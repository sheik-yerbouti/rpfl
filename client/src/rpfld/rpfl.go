package rpfld
import (
	"fmt"
	"os"
	"io/ioutil"
	"bytes"
	"net/http"
	"crypto/sha512"
	"github.com/agl/ed25519"
)

func hashLocal(path *string) (hash []byte, err error){
	bytes, err:= ioutil.ReadFile(*path);

	if err != nil{
		return nil, err
	}

	return sha512.New().Sum(bytes), nil;
}

func Verify() (verified bool){

	verifications := make(chan bool);

	for _, endpoint := range endpoints{
		go verifyInternal(&endpoint, nil, verifications)
	}

	for i := 0; i < len(endpoints); i++ {
		if !<-verifications {
			return false
		}
	}

	return true
}

func verifyInternal(endpoint *Endpoint, payload []byte, verifications chan bool){

	response, err := http.Post(endpoint.url.String(), "", bytes.NewReader(payload))

	defer response.Body.Close()
	responseBody, err := ioutil.ReadAll(response.Body)

	if err != nil {
		fmt.Printf("%s", err)
		os.Exit(1)
	}

	if len(responseBody) != 64{
		panic("hilfe!!!!")
	}

	var	signature [64]byte

	copy(signature[:], responseBody[0:64])

	verifications <- ed25519.Verify(&endpoint.publicKey, payload, &signature)
}