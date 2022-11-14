package io.stork.client.exceptions

class UnknownRemoteException(code: Int,
                             description: String) : StorkApiUncheckedException("Code $code: $description")