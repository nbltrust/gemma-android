// using namespace eosio::chain;
#include <string>
namespace eosio{
  class keosdlib;
}
// using namespace std;

class keosdlib_api {

// private:
//   eosio::keosdlib& k;
public:


    // needed by koofrank
    std::pair<std::string, std::string> createKey(const std::string& key_type) ;
    // needed by koofrank
    std::string get_private_key(const std::string& cipher, const std::string& password);
    std::string get_public_key(const std::string& priv_str );

    // needed by koofrank
    std::string get_cypher(const std::string& password, const std::string& priv_key);
    // Sign transaction

    std::string signTransaction_voteproducer(const std::string& priv_key_str, const std::string& contract, const std::string& voter_str, const std::string& infostr, 
        const std::string& abistr , uint32_t max_cpu_usage_ms, uint32_t max_net_usage_words , uint32_t tx_expiration = 120  );
    std::string signTransaction_delegatebw(const std::string& priv_key_str, const std::string& contract, const std::string& from_str, const std::string& infostr, 
        const std::string& abistr , uint32_t max_cpu_usage_ms, uint32_t max_net_usage_words , uint32_t tx_expiration = 120  );
    std::string signTransaction_undelegatebw(const std::string& priv_key_str, const std::string& contract, const std::string& from_str, 
              const std::string& infostr, const std::string& abistr , uint32_t max_cpu_usage_ms, uint32_t max_net_usage_words , uint32_t tx_expiration = 120  );
    std::string signTransaction_sellram(const std::string& priv_key_str, const std::string& contract, const std::string& account_str, 
              const std::string& infostr, const std::string& abistr , uint32_t max_cpu_usage_ms, uint32_t max_net_usage_words , uint32_t tx_expiration = 120  );

    std::string signTransaction_buyram(const std::string& priv_key_str, const std::string& contract, const std::string& payer_str, 
        const std::string& infostr, const std::string& abistr , uint32_t max_cpu_usage_ms, uint32_t max_net_usage_words , uint32_t tx_expiration = 120  );
    std::string signTransaction_tranfer(const std::string& priv_key_str, const std::string& contract, const std::string& senderstr, 
        const std::string& infostr, const std::string& abistr , uint32_t max_cpu_usage_ms, uint32_t max_net_usage_words , uint32_t tx_expiration = 120  );

    // create abi json
    std::string create_abi_req_buyram(const std::string& code, const std::string& action, const std::string& payer, const std::string& receiver, const std::string& quant );
    std::string create_abi_req_transfer(const std::string& code, const std::string& action, const std::string& from, const std::string& to, const std::string& quantity, const std::string& memo);
    std::string create_abi_req_undelegatebw(const std::string& code, const std::string& action, const std::string& from, const std::string& receiver, const std::string& unstake_net_quantity, const std::string& unstake_cpu_quantity );
    std::string create_abi_req_sellram(const std::string& code, const std::string& action, const std::string& account, uint32_t bytes);
    std::string create_abi_req_delegatebw(const std::string& code, const std::string& action, const std::string& from, const std::string& receiver, const std::string& stake_net_quantity, const std::string& stake_cpu_quantity );


};

