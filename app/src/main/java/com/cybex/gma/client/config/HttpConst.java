package com.cybex.gma.client.config;

/**
 * Http使用的常量
 */
public interface HttpConst {

    int CODE_RESULT_SUCCESS = 0;
    String RESULT_SUCCESS = "ok";
    int INVCODE_USED = 10002;
    int INVCODE_NOTEXIST = 10003;
    int EOSNAME_USED = 10004;
    int EOSNAME_INVALID = 10005;
    int EOSNAME_LENGTH_INVALID = 10006;
    int PARAMETERS_INVALID = 10007;
    int PUBLICKEY_INVALID = 10008;
    int INVALID_PARAMETER = 10013;
    int BALANCE_NOT_ENOUGH = 20001;
    int CREATE_ACCOUNT_FAIL = 20002;

    String KEY_ACCOUNT_NAME = "account_name";
    String KEY_SHOW_NUM = "show_num";
    String KEY_LAST_POST = "last_pos";
    int PAGE_NUM = 10;
    int ACTION_REFRESH = -1;//列表刷新操作

    int SERVER_INTERNAL_ERR = 500;

    int WXPAY_SUCCESS = 0;//微信支付成功
    int WXPAY_ERROR = -1;//微信支付错误
    int WXPAY_CANCEL = -2;//微信支付用户取消

    String WXPAY_STATE_NOTPAY = "NOTPAY";
    String WXPAY_STATE_SUCCESS = "SUCCESS";
    String WXPAY_STATE_REFUND = "REFUND";
    String WXPAY_STATUS_TOREFUND = "REFUND";
    String WXPAY_STATE_CLOSED = "CLOSED";
    String WXPAY_STATE_USERPAYING = "USERPAYING";
    String WXPAY_STATE_PAYERROR = "PAYERROR";

    String WXPAY_STATUS_INIT = "INIT";
    String WXPAY_STATUS_CLOSED = "CLOSED";
    String WXPAY_STATUS_DONE = "DONE";



    /**
     * --------------EOS 错误码---------------
     */

    /*
    int blockchain_exception = 3000000;
    int Invalid_name = 3010001;
    int Invalid_public_key = 3010002;
    int Invalid_private_key = 3010003;
    int Invalid_authority = 3010004;
    int Invalid_action = 3010005;
    int Invalid_transaction = 3010006;
    int Invalid_ABI = 3010007;
    int Invalid_block_ID = 3010008;
    int Invalid_transaction_ID = 3010009;
    int Invalid_packed_transaction = 3010010;
    int Invalid_asset = 3010011;
    int Invalid_chain_ID = 3010012;
    int Invalid_fixed_key = 3010013;
    int Invalid_symbol = 3010014;
    int Fork_database_exception = 3020000;
    int Block_can_not_be_found = 3020001;

    int Block_exception = 3030000;
    int Unlinkable_block = 3030001;
    int Transaction_outputs__in__block_do_not_match_transaction_outputs_from_applying_block = 3030002;
    int Block_does_not_guarantee_concurrent_execution_without_conflicts = 3030003;
    int Shard_locks_in_block_are_incorrect_or_mal_formed = 3030004;
    int Block_exhausted_allowed_resources = 3030005;
    int Block_is_too_old_to_push = 3030006;
    int Block_is_from_the_future = 3030007;
    int Block_is_not_signed_with_expected_key = 3030008;
    int Block_is_not_signed_by_expected_producer = 3030009;

    int Transaction_exception = 3040000;
    int Error_decompressing_transaction = 3040001;
    int Transaction_should_have_at_least_one_normal_action = 3040002;
    int Transaction_should_have_at_least_one_required_authority = 3040003;
    int Context_free_action_should_have_no_required_authority = 3040004;
    int Expired_Transaction = 3040005;
    int Transaction_Expiration_Too_Far = 3040006;
    int Invalid_Reference_Block = 3040007;
    int Duplicate_transaction = 3040008;
    int Duplicate_deferred_transaction = 3040009;
    int Context_free_action_is_not_allowed_inside_generated_Transaction = 3040010;
    int The_transaction_can_not_be_found = 3040011;
    int Pushing_too_many_transactions_at_once = 3040012;
    int Transaction_is_too_big = 3040013;
    int Unknown_transaction_compression = 3040014;

    int Action_validate_exception = 3050000;
    int Account_name_already_exists = 3050001;
    int Invalid_Action_Arguments = 3050002;
    int eosio_assert_message_assertion_failure = 3050003;
    int eosio_assert_code_assertion_failure = 3050004;
    int Action_can_not_be_found = 3050005;
    int Mismatch_between_action_data_and_its_struct = 3050006;
    int Attempt_to_use_unaccessible_API = 3050007;
    int Abort_Called = 3050008;
    int Inline_Action_exceeds_maximum_size_limit = 3050009;

    int Database_exception = 3060000;
    int Permission_Query_Exception = 3060001;
    int Account_Query_Exception = 3060002;
    int Contract_Table_Query_Exception = 3060003;
    int Contract_Query_Exception = 3060004;
    int Database_exception_two = 3060100;
    int Database_usage_is_at_unsafe_levels = 3060101;
    int Reversible_block_log_usage_is_at_unsafe_levels = 3060102;

    int WASM_Exception = 3070000;
    int Error_in_WASM_page_memory = 3070001;
    int Runtime_Error_Processing_WASM = 3070002;
    int Serialization_Error_Processing_WASM = 3070003;
    int memcpy_with_overlapping_memory = 3070004;
    int binaryen_exception = 3070005;

    int Resource_exhausted_exception = 3080000;
    int Account_using_more_than_allotted_RAM_usage = 3080001;
    int Transaction_exceeded_the_current_network_usage_limit_imposed_on_the_transaction = 3080002;
    int Transaction_network_usage_is_too_much_for_the_remaining_allowable_usage_of_the_current_block = 3080003;
    /*
    int Transaction_exceeded_the_current_CPU_usage_limit_imposed_on_the_transaction 3080004
    int Transaction_CPU_usage_is_too_much_for_the_remaining_allowable_usage_of_the_current_block 3080005
    int Transaction_took_too_long 3080006
    int Transaction_exceeded_the_current_greylisted_account_network_usage_limit 3080007

    int Transaction_reached_the_deadline_set_due_to_leeway_on_account_CPU_limits 3081001

    int Authorization_exception 3090000
    int Duplicate_signature_included 3090001
    int Irrelevant_signature_included 3090002
    int Provided_keys_permissions_and_delays_do_not_satisfy_declared_authorizations 3090003
    int Missing_required_authority 3090004
    int Irrelevant_authority_included 3090005
    int Insufficient_delay 3090006
    int Invalid_Permission 3090007
    int The_action_is_not_allowed_to_be_linked_with_minimum_permission 3090008
    int The_parent_permission_is_invalid 3090009

    int Miscellaneous_exception 3100000
    int Internal_state_is_no_longer_consistent 3100001
    int Unknown_bloc 3100002
    int Unknown_transaction 3100003
    int Corrupted_reversible_block_database_was_fixed 3100004
    int Extracted_genesis_state_from_blocks_log 3100005
    int Subjective_exception_thrown_during_block_production 3100006
    int Multiple_voter_info_detected 3100007
    int Feature_is_currently_unsupported 3100008
    int Node_management_operation_successfully_executed 3100009

    int Plugin_exception 3110000
    int Missing_Chain_API_Plugin 3110001
    int Missing_Wallet_API_Plugin 3110002
    int Missing_History_API_Plugin 3110003
    int Missing_Net_API_Plugin 3110004
    int Missing_Chain_Plugin 3110005
    int Incorrect_plugin_configuration 3110006

    int Wallet_exception 3120000
    int Wallet_already_exists 3120001
    int Nonexistent_wallet 3120002
    int Locked_wallet 3120003
    int Missing_public_key 3120004
    int Invalid_wallet_password 3120005
    int No_available_wallet 3120006
    int Already_unlocked 3120007
    int Key_already_exists 3120008
    int Nonexistent_key 3120009
    int Unsupported_key_type 3120010
    int Wallet_lock_timeout_is_invalid 3120011
    int Secure_Enclave_Exception 3120012

    int Actor_or_contract_whitelist_blacklist_exception 3130000
    int Authorizing_actor_of_transaction_is_not_on_the_whitelist 3130001
    int Authorizing_actor_of_transaction_is_on_the_blacklist 3130002
    int Contract_to_execute_is_not_on_the_whitelist 3130003
    int Contract_to_execute_is_on_the_blacklist 3130004
    int Action_to_execute_is_on_the_blacklist_ 3130005
    int Public_key_in_authority_is_on_the_blacklist 3130006

    int Exceptions_that_are_allowed_to_bubble_out_of_emit_calls_in_controller 3140000
    int Block_does_not_match_checkpoint 3140001

    int ABI_exception 3150000
    int No_ABI_found 3150001
    int Invalid_Ricardian_Clause 3150002
    int Invalid_Ricardian_Action 3150003
    int The_type_defined_in_the_ABI_is_invalid 3150004
    int Duplicate_type_definition_in_the_ABI 3150005
    int Duplicate_struct_definition_in_the_ABI 3150006
    int Duplicate_action_definition_in_the_ABI 3150007
    int Duplicate_table_definition_in_the_ABI 3150008
    int Duplicate_error_message_definition_in_the_ABI 3150009
    int ABI_serialization_time_has_exceeded_the_deadline 3150010
    int ABI_recursive_definition_has_exceeded_the_max_recursion_depth 3150011
    int Circular_definition_is_detected_in_the_ABI 3150012
    int Unpack_data_exception 3150013
    int Pack_data_exception 3150014

    int Contract_exception 3160000
    int The_payer_of_the_table_data_is_invalid 3160001
    int Table_access_violation 3160002
    int Invalid_table_iterator 3160003
    int Table_can_not_be_found_inside_the_cache 3160004
    int The_table_operation_is_not_allowed 3160005
    int Invalid_contract_vm_type 3160006
    int Invalid_contract_vm_version 3160007
    int Contract_is_already_running_this_version_of_code 3160008
    int No_wast_file_found 3160009
    int No_abi_file_found 3160010

    int Producer_exception 3170000
    int Producer_private_key_is_not_available 3170001
    int Pending_block_state_is_missing 3170002
    int Producer_is_double_confirming_known_rang 3170003
    int Producer_schedule_exception 3170004
    int The_producer_is_not_part_of_current_schedule 3170006

    int Reversible_Blocks_exception 3180000
    int Invalid_reversible_blocks_directory 3180001
    int Backup_directory_for_reversible_blocks_already_existg 3180002
    int Gap_in_the_reversible_blocks_database 3180003

    int Block_log_exception 3190000
    int unsupported_version_of_block_log 3190001
    int fail_to_append_block_to_the_block_log 3190002
    int block_log_can_not_be_found 3190003
    int block_log_backup_dir_already_exists 3190004

    int http_exception 3200000
    int invalid_http_client_root_certificate 3200001
    int invalid_http_response 3200002
    int service_resolved_to_multiple_ports 3200003
    int fail_to_resolve_host 3200004
    int http_request_fail 3200005
    int invalid_http_request 3200006

    int Resource_limit_exception 3210000
    int Mongo_DB_exception 3220000
    int Fail_to_insert_new_data_to_Mongo_DB 3220001
    int Fail_to_update_existing_data_in_Mongo_DB 3220002
    int Contract_API_exception 3230000
    int Crypto_API_Exception 3230001
    int Database_API_Exception 3230002
    int Arithmetic_Exception 3230003
*/


}
