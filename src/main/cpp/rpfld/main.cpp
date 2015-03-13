#include <iostream>
#include <set>
#include <string>
#include "dbus-c++-1/dbus-c++/dbus.h"
#include "snappy-c.h"
#include "boost/thread.hpp"
#include "Messages.pb.h"
#include <curlpp/cURLpp.hpp>
#include <curlpp/Easy.hpp>
#include <curlpp/Options.hpp>
#include <curlpp/Exception.hpp>
#include <malloc.h>

int main(int argc, char **argv) {
  boost::thread runner {run};
  return 0;
}

void run(){
  DBus::_init_threading();
  DBus::Dispatcher::pointer dispatcher = DBus::Dispatcher::enter();
  
  DBus::Connection::pointer conn = dispatcher->create_connection(DBus::BUS_SESSION);
  
  int ret = conn->request_name( "rpfl.rpfld", DBUS_NAME_FLAG_REPLACE_EXISTING );
  
  DBus::Object::pointer object = conn->create_object("/rpfl/rpfld");
  
  object->create_method<std::set<string>("rpfl.rpfld", "prepare", sigc::ptr_fun(prepare) );
}

void prepare(std::set<string> urls){
  try {
    org::rpfl::transport::protobuf::Request requestMessage;
    
    requestMessage.New();
    
    for(auto url: urls){
      requestMessage.add_resources(url);
    }
    
    int size = requestMessage.ByteSize(); 
    char* uncompressed = malloc(size);
    char* compressed = malloc(size);
    
    requestMessage.SerializeToArray(uncompressed, size);
    
    size_t* compressed_length;
    
    snappy_compress(uncompressed, size, compressed, compressed_length);
    
    curlpp::Cleanup cleaner;
    curlpp::Easy request;
    
    request.setOpt(new curlpp::options::Url(url)); 
    request.setOpt(new curlpp::options::Verbose(true)); 
    
    std::list<std::string> header; 
    header.push_back("Content-Type: application/octet-stream");     
    request.setOpt(new curlpp::options::HttpHeader(header)); 
    request.setOpt(new curlpp::options::Post(compressed));
    request.setOpt(new curlpp::options::PostFieldSize(5));
   
    
    request.perform(); 
    
   
  }
  catch ( curlpp::LogicError & e ) {
    std::cout << e.what() << std::endl;
  }
  catch ( curlpp::RuntimeError & e ) {
    std::cout << e.what() << std::endl;
  }
}

 


