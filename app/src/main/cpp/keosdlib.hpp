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

    // needed by koofrank
    std::string get_cypher(const std::string& password, const std::string& priv_key);
    // Sign transaction
    std::string signTransaction(const std::string& trxstr, const std::string& priv_key_str, const std::string& chain_id_str);
    std::string signTransaction(const std::string& priv_key_str, const std::string& contract, const std::string& senderstr, const std::string& recipientstr, const std::string& amountstr,
        const std::string& memo, const std::string& infostr, const std::string& abistr , unsigned int max_cpu_usage_ms, unsigned int max_net_usage_words , unsigned int tx_expiration = 120 );
    std::string create_abi_req(const std::string& code, const std::string& action, const std::string& from, const std::string& to, const std::string& quantity, const std::string& memo);

};

