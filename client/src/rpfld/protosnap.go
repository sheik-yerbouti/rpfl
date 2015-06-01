package rpfld
import (
	"org_rpfl_transport_protobuf"
	"github.com/golang/protobuf/proto"
	"github.com/google/go-snappy/snappy"
)

func createPayload(urls []string)(payload []byte, err error){
	request := &org_rpfl_transport_protobuf.Request{Resources:urls}

	marshalled, err := proto.Marshal(request)

	if err != nil{
		return nil, err
	}

	payload = make([]byte, snappy.MaxEncodedLen(len(marshalled)))

	snappy.Encode(payload, marshalled)

	return payload, nil
}